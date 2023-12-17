package com.example.english.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FolderListAdapter
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Models.Folder
import com.example.english.Models.Topic
import com.example.english.R
import com.example.english.ViewModels.FolderVM
import com.example.english.ViewModels.TopicVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class HomeFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val emailUser: String = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var rcvTopic: RecyclerView
    private lateinit var rcvFolder: RecyclerView

    private var topicList: ArrayList<TopicVM> = arrayListOf<TopicVM>()
    private var folderList: ArrayList<FolderVM> = arrayListOf<FolderVM>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_home, container, false)

        rcvTopic = mView.findViewById(R.id.rcvTopic)
        rcvTopic.layoutManager = LinearLayoutManager(mView.context, LinearLayoutManager.HORIZONTAL, false)
        rcvTopic.setHasFixedSize(true)
        rcvFolder = mView.findViewById(R.id.rcvFolder)
        rcvFolder.layoutManager = LinearLayoutManager(mView.context, LinearLayoutManager.HORIZONTAL, false)
        rcvFolder.setHasFixedSize(true)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic")
                    .whereEqualTo("isDelete", false)
                    .whereEqualTo("email", emailUser)
                    .get().await()
                val querySnapshot1 = db.collection("folder")
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

                for (document in querySnapshot1) {
                    val documentId = document.id

                    val folderVMNew = FolderVM()

                    val folder = document.toObject(Folder::class.java)

                    val folderTopicSnapshot =
                        db.collection("folder-topic")
                            .whereEqualTo("folderId", documentId)
                            .whereEqualTo("isDelete", false)
                            .get().await()

                    folderVMNew.folderId = documentId
                    folderVMNew.heading = folder.name
                    folderVMNew.countTopics = folderTopicSnapshot.size()
                    folderVMNew.emailUser = folder.email

                    folderList.add(folderVMNew)
                }

                rcvTopic.adapter = TopicListAdapter(topicList)
                rcvFolder.adapter = FolderListAdapter(mView.context, folderList)
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }

        return mView
    }

}