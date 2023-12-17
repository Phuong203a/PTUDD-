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
                        showTopic(newText!!)
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

    private suspend fun showTopic(title: String?) {
        recycleView.layoutManager = LinearLayoutManager(this.context)
        recycleView.setHasFixedSize(true)

        val querySnapshot = db.collection("topic")
            .whereEqualTo("isPublic", true)
            .whereEqualTo("title", title)
            .get().await()
        var topicList = arrayListOf<TopicVM>()
        for (document in querySnapshot) {
            val documentId = document.id

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

        recycleView.adapter = this.context?.let { TopicListAdapter(topicList) }

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