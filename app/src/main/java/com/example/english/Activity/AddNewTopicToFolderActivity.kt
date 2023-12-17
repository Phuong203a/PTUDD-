package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FolderListAdapter
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Adapter.TopicListForActionAdapter

import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddNewTopicToFolderActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var editTextSearch: SearchView
    private var searchJob: Job? = null
    private lateinit var recycleView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_add_new_topic_to_folder)
        editTextSearch = findViewById(R.id.editTxtSearchTopic)
        val folderId = intent.getStringExtra("folderId")
        recycleView = findViewById(R.id.recycleTopicListSearch)

        editTextSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    showTopic(newText!!, folderId)
                }
                return true
            }
        })
    }

    private suspend fun showTopic(title: String?, folderId: String?) {
        recycleView.layoutManager = LinearLayoutManager(this.applicationContext)
        recycleView.setHasFixedSize(true)

        val querySnapshot = db.collection("topic")
            .whereEqualTo("isPublic", true)
            .whereEqualTo("title", title)
            .get().await()
        var topicList = arrayListOf<TopicVM>()
        for (document in querySnapshot) {
            val documentId = document.id
            if (checkIfTopicExistInFolder(documentId, folderId)) {
                continue
            }
            val topicVMNew = TopicVM()

            val topic = document.toObject(Topic::class.java)

            val vocabularySnapshot =
                db.collection("topic")
                    .document(documentId)
                    .collection("vocabulary").get().await()

            topicVMNew.countWords = vocabularySnapshot.size()
            topicVMNew.title = topic.title
            topicVMNew.emailUser = topic.email
            topicVMNew.id = documentId

            topicList.add(topicVMNew)
        }

        recycleView.adapter =
            this.applicationContext?.let {
                TopicListForActionAdapter(
                    this,
                    this,
                    topicList,
                    folderId!!
                )
            }

    }

    private suspend fun checkIfTopicExistInFolder(topicId: String?, folderId: String?): Boolean {
        try {
            val querySnapshot = db.collection("folder-topic")
                .whereEqualTo("folderId", folderId)
                .whereEqualTo("topicId", topicId)
                .get()
            val result = querySnapshot.await()
            if (result.isEmpty) {
                return false
            }
        } catch (ex: Exception) {
            Log.d("Exception checkIfTopicExistInFolder", ex.message ?: "")
        }
        return true
    }

}