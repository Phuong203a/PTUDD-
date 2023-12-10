package com.example.english.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FolderListAdapter
import com.example.english.R
import com.example.english.ViewModels.FolderVM
import com.example.english.ViewModels.TopicVM

class FolderLibraryFragment : Fragment() {
    private lateinit var rcvFolderList: RecyclerView
    private var folderList: ArrayList<FolderVM> = arrayListOf<FolderVM>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_folder_library, container, false)

        rcvFolderList = mView.findViewById(R.id.rcvFolderList)

        rcvFolderList.layoutManager = LinearLayoutManager(mView.context)
        rcvFolderList.setHasFixedSize(true)

        getData()

        return mView
    }

    private fun getData() {
        val headingList = arrayOf(
            "Heading 1",
            "Heading 2",
            "Heading 3",
            "Heading 4",
            "Heading 5",
            "Heading 6",
            "Heading 7",
            "Heading 8",
            "Heading 9",
            "Heading 10",
        )

        val emailList = arrayOf(
            "Heading 1@gmail.com",
            "Heading 2@gmail.com",
            "Heading 3@gmail.com",
            "Heading 4@gmail.com",
            "Heading 5@gmail.com",
            "Heading 6@gmail.com",
            "Heading 7@gmail.com",
            "Heading 8@gmail.com",
            "Heading 9@gmail.com",
            "Heading 10@gmail.com",
        )

        for (i in headingList.indices) {
            val item = FolderVM(headingList[i], 10, "", emailList[i])
            folderList.add(item)
        }

        rcvFolderList.adapter = FolderListAdapter(folderList)
    }

}