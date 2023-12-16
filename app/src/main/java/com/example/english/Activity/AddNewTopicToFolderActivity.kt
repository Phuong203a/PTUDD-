package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FolderListAdapter
import com.example.english.Adapter.TopicListAdapter

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
        editTextSearch= findViewById(R.id.editTxtSearchTopic)
        val folderId = intent.getStringExtra("folderId")
        recycleView= findViewById(R.id.recycleTopicListSearch)

        editTextSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    var topics = getAllTopicByName(newText)
                    showTopic(topics)
                }
                return true
            }
        })
    }

    private fun showTopic(topics: List<Topic>) {
        Log.d("Topic size", "$topics.size")
        recycleView.layoutManager = LinearLayoutManager(this.applicationContext)
        recycleView.setHasFixedSize(true)

        val topicVmList: ArrayList<TopicVM> = arrayListOf()
        for (i in topics.indices) {
            val item = TopicVM(topics[i].title, 10, "", topics[i].email)
            topicVmList.add(item)
        }

        recycleView.adapter = this.applicationContext?.let { TopicListAdapter(topicVmList) }

    }
    private suspend fun getAllTopicByName(title: String?): List<Topic> {
        val emptyList: List<Vocabulary> = listOf()
        val allTopic = mutableListOf<Topic>()
        try {
            val firebaseTask = db.collection("topic")
                .whereEqualTo("isPublic", true)
                .whereEqualTo("title", title)
                .get()

            val result = firebaseTask.await()

            result.forEach { r ->
                val topic = Topic(
                    r.id,
                    r.getString("title") ?: "",
                    r.getBoolean("isPublic") ?: false,
                    r.getString("email") ?: "",
                    emptyList
                )
                allTopic.add(topic)
            }
        } catch (ex: Exception) {
            Log.d("Exception getAllTopicByName", ex.message ?: "")
        }
        return allTopic
    }

}