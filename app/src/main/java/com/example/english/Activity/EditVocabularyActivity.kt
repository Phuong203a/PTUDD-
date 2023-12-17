package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditVocabularyActivity : AppCompatActivity() {
    private val topicCollection = FirebaseFirestore.getInstance().collection("topic")

    private lateinit var tvExit: TextView
    private lateinit var tvEdit: TextView
    private lateinit var edtFrontSide: EditText
    private lateinit var edtBackSide: EditText

    private var vocabulary = Vocabulary()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vocabulary)

        init()

        val topicId = intent.getStringExtra("topicId")
        val vocabularyId = intent.getStringExtra("vocabularyId")
        if (topicId != null && vocabularyId != null) {
            getVocabulary(topicId, vocabularyId)
        }

        tvExit.setOnClickListener {
            finish()
        }

        tvEdit.setOnClickListener {
            if (topicId != null && vocabularyId != null) {
                handleEdit(vocabulary, vocabularyId, topicId)
            }
        }
    }

    private fun init() {
        tvExit = findViewById(R.id.tvExit)
        tvEdit = findViewById(R.id.tvEdit)
        edtFrontSide = findViewById(R.id.edtFrontSide)
        edtBackSide = findViewById(R.id.edtBackSide)
    }

    private fun handleEdit(vocabulary: Vocabulary, vocabularyId: String, topicId: String) {
        vocabulary.words = edtFrontSide.text.toString()
        vocabulary.meaning = edtBackSide.text.toString()

        val updatedVocabulary = hashMapOf(
            "words" to vocabulary.words.toString(),
            "meaning" to vocabulary.meaning.toString(),
        )

        val vocabularyDocument = topicCollection
                            .document(topicId)
                            .collection("vocabulary")
                            .document(vocabularyId)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                vocabularyDocument?.update(updatedVocabulary as Map<String, Any>)?.await()

                finish()

            } catch (e: java.lang.Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun getVocabulary(topicId: String, vocabularyId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (topicId != null) {
                    val querySnapshot = topicCollection
                        .document(topicId)
                        .collection("vocabulary")
                        .document(vocabularyId)
                        .get().await()

                    vocabulary = querySnapshot.toObject(Vocabulary::class.java)!!

                    edtFrontSide.setText(vocabulary.words)
                    edtBackSide.setText(vocabulary.meaning)
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }
}