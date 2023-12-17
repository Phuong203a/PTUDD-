package com.example.english.Activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AddVocabularyActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var tvExit:TextView
    private lateinit var tvAdd:TextView
    private lateinit var edtFrontSide:EditText
    private lateinit var edtBackSide:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vocabulary)

        val topicId = intent.getStringExtra("topicId")
        init()

        tvExit.setOnClickListener {
            finish()
        }

        tvAdd.setOnClickListener {
            val vocabulary = Vocabulary(edtFrontSide.text.toString(), edtBackSide.text.toString(), false)
            handleAdd(topicId, vocabulary)
        }
    }

    private fun init() {
        tvExit = findViewById(R.id.tvExit)
        tvAdd = findViewById(R.id.tvAdd)
        edtFrontSide = findViewById(R.id.edtFrontSide)
        edtBackSide = findViewById(R.id.edtBackSide)
    }

    private fun handleAdd(topicId: String?, vocabulary: Vocabulary) {
        val newVocabulary = hashMapOf(
            "words" to vocabulary.words,
            "meaning" to vocabulary.meaning,
            "isDelete" to vocabulary.isDelete
        )

        val vocabularyCollection = topicId?.let { db.collection("topic").document(it).collection("vocabulary") }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val vocabularyDocumentNew = vocabularyCollection?.add(newVocabulary)?.await()

                Toast.makeText(applicationContext, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Log.e("tag", e.toString())
                Toast.makeText(applicationContext, "Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}