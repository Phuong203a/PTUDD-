package com.example.english.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Adapter.VocabularyListAdapter
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.example.english.ViewModels.VocabularyVM
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TopicVocabularyListActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var tvTitle: TextView
    private lateinit var tvMode: TextView
    private lateinit var ivEdit: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var rcvVocabularyList: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var cvFlashcard: CardView
    private lateinit var cvObjectiveTest: CardView
    private lateinit var cvFillWords: CardView

    private var vocabularyList: ArrayList<VocabularyVM> = arrayListOf<VocabularyVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_vocabulary_list)

        val topicId = intent.getStringExtra("topicId")

        init()

        if (topicId != null) {
            showData(topicId)
        }

        cvFlashcard.setOnClickListener {
            val intent = Intent(this, FlashcardSettingActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        cvObjectiveTest.setOnClickListener {
            val intent = Intent(this, ObjectiveTestActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        cvFillWords.setOnClickListener {
            val intent = Intent(this, FillWordsActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }


    }

    private fun init() {
        tvTitle = findViewById(R.id.tvTitle)
        tvMode = findViewById(R.id.tvMode)
        ivEdit = findViewById(R.id.ivEdit)
        ivDelete = findViewById(R.id.ivDelete)
        rcvVocabularyList = findViewById(R.id.rcvVocabularyList)
        btnAdd = findViewById(R.id.btnAdd)
        cvFlashcard = findViewById(R.id.cvFlashcard)
        cvObjectiveTest = findViewById(R.id.cvObjectiveTest)
        cvFillWords = findViewById(R.id.cvFillWords)

        rcvVocabularyList.layoutManager = LinearLayoutManager(this)
        rcvVocabularyList.setHasFixedSize(true)
    }

    private fun showData(topicId: String) {
        showDataTopic(topicId)
        showDataVocabulary(topicId)
    }

    private fun showDataTopic(topicId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic").document(topicId).get().await()
                val topic = querySnapshot.toObject(Topic::class.java)

                if (topic != null) {
                    tvTitle.text = topic.title
                    tvMode.text = if (topic.isPublic == true) "Công khai" else "Riêng tư"
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun showDataVocabulary(topicId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic").document(topicId).collection("vocabulary").get().await()

                for (document in querySnapshot) {
                    val vocabularyVMNew = VocabularyVM()

                    val vocabulary = document.toObject(Vocabulary::class.java)

                    vocabularyVMNew.words = vocabulary.words
                    vocabularyVMNew.meaning = vocabulary.meaning

                    vocabularyList.add(vocabularyVMNew)
                }

                    rcvVocabularyList.adapter = VocabularyListAdapter(vocabularyList)
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }
}