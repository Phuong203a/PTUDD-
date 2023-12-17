package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.english.Models.ObjectiveTest
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


class ObjectiveTestQuestionActivity : AppCompatActivity() {
    private val topicCollection = FirebaseFirestore.getInstance().collection("topic")

    private lateinit var tvCurrentQuestion: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvQuestion: TextView
    private lateinit var btnAnswer1: AppCompatButton
    private lateinit var btnAnswer2: AppCompatButton
    private lateinit var btnAnswer3: AppCompatButton
    private lateinit var btnAnswer4: AppCompatButton
    private lateinit var btnNext: Button

    private var currentQuestion = 0
    private var scorePlayer = 0
    private var indexChoose = -1
    private var isClickBtn = false
    private lateinit var btnClick: AppCompatButton
    private var questionList: ArrayList<ObjectiveTest> = arrayListOf<ObjectiveTest>()
    private var vocabularyList: ArrayList<Vocabulary> = arrayListOf<Vocabulary>()


    private val buttons: List<Button> by lazy {
        listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objective_test_question)

        val topicId = intent.getStringExtra("topicId")
        val isReverse = intent.getBooleanExtra("reverse", false)
        val isFeedback = intent.getBooleanExtra("feedback", false)

        init()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (topicId != null) {
                    val querySnapshot = topicCollection.document(topicId).collection("vocabulary").get().await()

                    for (document in querySnapshot) {
                        vocabularyList.add(document.toObject(Vocabulary::class.java))
                    }

                    questionList = randomQuestion(vocabularyList, isReverse)
                    questionList.shuffle()
                    remplirData()
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }

        btnAnswer1.setOnClickListener { onButtonClick(btnAnswer1) }
        btnAnswer2.setOnClickListener { onButtonClick(btnAnswer2) }
        btnAnswer3.setOnClickListener { onButtonClick(btnAnswer3) }
        btnAnswer4.setOnClickListener { onButtonClick(btnAnswer4) }

        ivBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            if (isClickBtn) {
                isClickBtn = false
                var correctIndex = questionList[currentQuestion].correctIndex
                if (indexChoose == correctIndex) {
                    if (isFeedback) {
                        btnClick.setBackgroundResource(R.drawable.background_btn_correct)
                    }
                    scorePlayer++
                } else {
                    if (isFeedback) {
                        btnClick.setBackgroundResource(R.drawable.background_btn_incorrect)
                        correctIndex = correctIndex!! - 1
                        buttons[correctIndex].apply {
                            post {
                                setBackgroundResource(R.drawable.background_btn_correct)
                            }
                        }
                    }
                }

                questionList[currentQuestion].currentAnswer = indexChoose

                Handler(Looper.getMainLooper()).postDelayed({
                    if (currentQuestion != questionList.size - 1) {
                        currentQuestion++

                        remplirData()

                        indexChoose = -1

                        btnAnswer1.setBackgroundResource(R.drawable.background_btn_choose)
                        btnAnswer2.setBackgroundResource(R.drawable.background_btn_choose)
                        btnAnswer3.setBackgroundResource(R.drawable.background_btn_choose)
                        btnAnswer4.setBackgroundResource(R.drawable.background_btn_choose)
                    } else {
                        val intent = Intent(this, ResultObjectiveTestActivity::class.java)
                        intent.putExtra("score", scorePlayer)
                        val bundle = Bundle().apply {
                            putSerializable("questionList", questionList)
                        }
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)
            } else {
                Toast.makeText(this , "Bạn chưa chọn đáp án",Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun init() {
        tvCurrentQuestion = findViewById(R.id.tvCurrentQuestion)
        ivBack = findViewById(R.id.ivBack)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnAnswer1 = findViewById(R.id.btnAnswer1)
        btnAnswer2 = findViewById(R.id.btnAnswer2)
        btnAnswer3 = findViewById(R.id.btnAnswer3)
        btnAnswer4 = findViewById(R.id.btnAnswer4)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun randomQuestion(vocabularyList: ArrayList<Vocabulary>, isReverse: Boolean): ArrayList<ObjectiveTest> {
        var questionList: ArrayList<ObjectiveTest> = arrayListOf<ObjectiveTest>()

        for (vocabulary in vocabularyList) {
            val question = ObjectiveTest()
            if (isReverse) {
                question.question = vocabulary.meaning

                val meanings = vocabularyList.map { it.words }.toMutableList()
                meanings.remove(vocabulary.words)

                question.correctIndex = Random.nextInt(1, 5)

                when(question.correctIndex) {
                    1 -> {
                        question.answer1 = vocabulary.words
                        question.answer2 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    2 -> {
                        question.answer2 = vocabulary.words
                        question.answer1 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    3 -> {
                        question.answer3 = vocabulary.words
                        question.answer1 = meanings[0]
                        question.answer2 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    4 -> {
                        question.answer4 = vocabulary.words
                        question.answer2 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer1 = meanings[2]
                    }
                }
            } else {
                question.question = vocabulary.words

                val meanings = vocabularyList.map { it.meaning }.toMutableList()
                meanings.remove(vocabulary.meaning)

                meanings.shuffle()

                question.correctIndex = Random.nextInt(1, 5)

                when(question.correctIndex) {
                    1 -> {
                        question.answer1 = vocabulary.meaning
                        question.answer2 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    2 -> {
                        question.answer2 = vocabulary.meaning
                        question.answer1 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    3 -> {
                        question.answer3 = vocabulary.meaning
                        question.answer1 = meanings[0]
                        question.answer2 = meanings[1]
                        question.answer4 = meanings[2]
                    }
                    4 -> {
                        question.answer4 = vocabulary.meaning
                        question.answer2 = meanings[0]
                        question.answer3 = meanings[1]
                        question.answer1 = meanings[2]
                    }
                }
            }

            questionList.add(question)
        }

        return questionList
    }

    private fun remplirData() {
        val current = questionList[currentQuestion]

        tvCurrentQuestion.text = "${currentQuestion + 1} / ${questionList.size}"
        tvQuestion.text = current.question
        btnAnswer1.text = current.answer1
        btnAnswer2.text = current.answer2
        btnAnswer3.text = current.answer3
        btnAnswer4.text = current.answer4
    }

    private fun onButtonClick(clickedButton: AppCompatButton) {
        btnClick = clickedButton

        clickedButton.setBackgroundResource(R.drawable.background_btn_choose_color)

        buttons.filter { it != clickedButton }.forEach {
            it.setBackgroundResource(R.drawable.background_btn_choose)
        }

        handleButtonClick(clickedButton)
    }

    private fun handleButtonClick(clickedButton: AppCompatButton) {
        isClickBtn = true

        when (clickedButton) {
            btnAnswer1 -> indexChoose = 1
            btnAnswer2 -> indexChoose = 2
            btnAnswer3 -> indexChoose = 3
            btnAnswer4 -> indexChoose = 4
        }
    }
}