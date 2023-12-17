package com.example.english.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.ViewModels.TopicVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TopicLibraryFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val emailUser: String = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var rcvTopicList: RecyclerView

    private var topicList: ArrayList<TopicVM> = arrayListOf<TopicVM>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_topic_library, container, false)

        rcvTopicList = mView.findViewById(R.id.rcvTopicList)
        rcvTopicList.layoutManager = LinearLayoutManager(mView.context)
        rcvTopicList.setHasFixedSize(true)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic")
                    .whereEqualTo("isDelete", false)
                    .whereEqualTo("email", emailUser)
                    .get().await()

                for (document in querySnapshot) {
                    val documentId = document.id

                    val topicVMNew = TopicVM()

                    val topic = document.toObject(Topic::class.java)

                    val vocabularySnapshot =
                        db.collection("topic")
                            .document(documentId)
                            .collection("vocabulary")
                            .whereEqualTo("isDelete", false)
                            .get().await()

                    topicVMNew.countWords = vocabularySnapshot.size()
                    topicVMNew.title = topic.title
                    topicVMNew.emailUser = emailUser
                    topicVMNew.id = documentId

                    topicList.add(topicVMNew)
                }

                rcvTopicList.adapter = TopicListAdapter(topicList)
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }

//        db.collection("topic")
//            .whereEqualTo("email", emailUser).get()
//            .addOnSuccessListener {
//                if (it != null) {
//                    for (document in it) {
//                        val topicVMNew: TopicVM = TopicVM()
//
//                        val topic = document.toObject(Topic::class.java)
//
//                        val vocabulary = db.collection("topic").document(document.id)
//                            .collection("vocabulary").get()
//
//                        vocabulary.addOnSuccessListener { it2 ->
//                            if (it2 != null) {
//                                topicVMNew.countWords = it2.size()
//
//                                topicVMNew.title = topic.title
//                                topicVMNew.emailUser = emailUser
//                                topicVMNew.countWords = topic.vocabularyList?.size ?: 0
//
//                                topicList.add(topicVMNew)
//                            }
//
//                        }
//                    }
//
//                    rcvTopicList.adapter = TopicListAdapter(topicList)
//                }
//            }

//        rcvTopicList.adapter = TopicListAdapter(topicList)
        return mView
    }

}