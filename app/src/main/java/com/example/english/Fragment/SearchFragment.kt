package com.example.english.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Adapter.UserListAdapter
import com.example.english.Models.Topic
import com.example.english.Models.User
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var searchViewAll: SearchView
    private var searchJob: Job? = null
    private lateinit var recycleView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mView =  inflater.inflate(R.layout.fragment_search, container, false)
        db = FirebaseFirestore.getInstance()
        searchViewAll= mView.findViewById(R.id.searchViewAll)
        recycleView = mView.findViewById(R.id.recycleSearchAll)

        searchViewAll.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if(!isEmailValid(newText)){
                        var topics = getAllTopicByName(newText)
                        showTopic(topics)
                    }else{
                        var users = getAllUserByEmail(newText)
                        showUser(users)
                    }

                }
                return true
            }
        })

        return mView
    }

    private fun showTopic(topics: List<Topic>) {
        Log.d("Topic size", "$topics")
        recycleView.layoutManager = LinearLayoutManager(this.context)
        recycleView.setHasFixedSize(true)

        val topicVmList: ArrayList<TopicVM> = arrayListOf()
        for (i in topics.indices) {
            val item = TopicVM(topics[i].title, 10, "", topics[i].email)
            topicVmList.add(item)
        }

        recycleView.adapter = this.context?.let { TopicListAdapter(topicVmList) }

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


    private fun showUser(users: List<User>) {
        Log.d("User size", "$users")
        recycleView.layoutManager = LinearLayoutManager(this.context)
        recycleView.setHasFixedSize(true)
        recycleView.adapter = this.context?.let { UserListAdapter(users) }

    }
    private suspend fun getAllUserByEmail(email: String?): List<User> {
        val allUser = mutableListOf<User>()
        try {
            val firebaseTask = db.collection("users")
                .whereEqualTo("email", email)
                .get()

            val result = firebaseTask.await()

            result.forEach { r ->
                val user = User(
                    r.getString("email") ?: "",
                    r.getString("name") ?: "",
                    "",
                    r.getString("avatar") ?: "",
                )
                allUser.add(user)
            }
        } catch (ex: Exception) {
            Log.d("Exception getAllTopicByName", ex.message ?: "")
        }
        return allUser
    }
    fun isEmailValid(email: String?): Boolean {
        val emailPattern = Regex("^\\S+@\\S+\\.\\S+\$") // Regular expression for a basic email pattern

        return emailPattern.matches(email!!)
    }
}