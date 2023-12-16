package com.example.english.Activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.english.Models.FillWords
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FillWordsQuestionActivity : AppCompatActivity() {
    private val topicCollection = FirebaseFirestore.getInstance().collection("topic")

    private lateinit var tvCurrentQuestion: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvQuestion: TextView
    private lateinit var edtWordsFill: EditText
    private lateinit var btnNext: Button

    private var currentQuestion = 0
    private var scorePlayer = 0
    private var fillWordsQuestion: ArrayList<FillWords> = arrayListOf<FillWords>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_words_question)

        val topicId = intent.getStringExtra("topicId")
        val isReverse = intent.getBooleanExtra("reverse", false)
        val isFeedback = intent.getBooleanExtra("feedback", false)

        init()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (topicId != null) {
                    val querySnapshot = topicCollection.document(topicId).collection("vocabulary").get().await()

                    for (document in querySnapshot) {
                        val vocabulary = document.toObject(Vocabulary::class.java)

                        val fillWords = if (isReverse) FillWords(vocabulary.words, vocabulary.meaning, "")
                                        else FillWords(vocabulary.meaning, vocabulary.words, "")

                        fillWordsQuestion.add(fillWords)
                    }

                    fillWordsQuestion.shuffle()
                    remplirData()
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }

        ivBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            val textWords = edtWordsFill.text.toString()

            if (textWords.isEmpty()) {
                Toast.makeText(this, "Chưa điền câu trả lời", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fillWords = fillWordsQuestion[currentQuestion]
            fillWords.wordsFill = textWords

            if (fillWords.equals()) scorePlayer++

            if (isFeedback) {
                showAlertDialog(this, fillWords.wordsCorrect.toString(), fillWords.equals())
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (currentQuestion != fillWordsQuestion.size - 1) {
                    currentQuestion++

                    remplirData()
                } else {
                    val intent = Intent(this, ResultFillWordsActivity::class.java)
                    intent.putExtra("score", scorePlayer)
                    val bundle = Bundle().apply {
                        putSerializable("fillWordsList", fillWordsQuestion)
                    }
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }
            }, 3000)
        }
    }

    private fun init() {
        tvCurrentQuestion = findViewById(R.id.tvCurrentQuestion)
        ivBack = findViewById(R.id.ivBack)
        tvQuestion = findViewById(R.id.tvQuestion)
        edtWordsFill = findViewById(R.id.edtWordsFill)
        btnNext = findViewById(R.id.btnNext)
    }
    private fun remplirData() {
        val current = fillWordsQuestion[currentQuestion]

        tvCurrentQuestion.text = "${currentQuestion + 1} / ${fillWordsQuestion.size}"
        tvQuestion.text = current.question
        edtWordsFill.text.clear()
    }
    private fun showAlertDialog(context: Context, correctWords: String, isTrue: Boolean) {
        val builder = AlertDialog.Builder(context)

        val textView = TextView(context)

        textView.text = if (isTrue) "Câu trả lời chính xác" else "Câu trả lời bị sai, đáp án đúng là: $correctWords"
        textView.setTextColor(if (isTrue) Color.GREEN else Color.RED)
        textView.setPadding(20, 20, 20, 20)
        textView.textSize = 18f

        builder.setView(textView)

        builder.setPositiveButton("", null)


        val alertDialog = builder.create()
        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }


}