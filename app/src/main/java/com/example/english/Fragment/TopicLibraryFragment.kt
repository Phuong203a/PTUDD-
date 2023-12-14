package com.example.english.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Models.Folder
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.ViewModels.TopicVM
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.concurrent.FutureTask

class TopicLibraryFragment : Fragment() {
    private lateinit var rcvTopicList: RecyclerView
    private var topicList: ArrayList<TopicVM> = arrayListOf<TopicVM>()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance()
        val mView = inflater.inflate(R.layout.fragment_topic_library, container, false)

        rcvTopicList = mView.findViewById(R.id.rcvTopicList)

        rcvTopicList.layoutManager = LinearLayoutManager(mView.context)
        rcvTopicList.setHasFixedSize(true)

        getData()

        return mView
    }


    private fun getData() {
        val headingList = arrayOf(
            "Title 1",
            "Title 2",
            "Title 3",
            "Title 4",
            "Title 5",
            "Title 6",
            "Title 7",
            "Title 8",
            "Title 9",
            "Title 10",
            )

        val emailList = arrayOf(
            "Title 1@gmail.com",
            "Title 2@gmail.com",
            "Title 3@gmail.com",
            "Title 4@gmail.com",
            "Title 5@gmail.com",
            "Title 6@gmail.com",
            "Title 7@gmail.com",
            "Title 8@gmail.com",
            "Title 9@gmail.com",
            "Title 10@gmail.com",
        )

        for (i in headingList.indices) {
            val item = TopicVM(headingList[i], 10, "", emailList[i])
            topicList.add(item)
        }

        rcvTopicList.adapter = TopicListAdapter(topicList)
    }




}