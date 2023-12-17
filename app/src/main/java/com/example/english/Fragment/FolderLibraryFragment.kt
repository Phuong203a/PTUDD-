package com.example.english.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FolderListAdapter
import com.example.english.Models.Folder
import com.example.english.R
import com.example.english.ViewModels.FolderVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FolderLibraryFragment : Fragment() {
    private lateinit var rcvFolderList: RecyclerView
    private var folderList: ArrayList<FolderVM> = arrayListOf()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance()
        val mView = inflater.inflate(R.layout.fragment_folder_library, container, false)

        rcvFolderList = mView.findViewById(R.id.rcvFolderList)

        rcvFolderList.layoutManager = LinearLayoutManager(mView.context)
        rcvFolderList.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            async { getData() }
        }

        return mView
    }

    private suspend fun getAllFolderOfUser(): List<Folder> {

        val allFolder = mutableListOf<Folder>()
        try {
            val firebaseTask = db.collection("folder")
                .whereEqualTo("isDelete", false)
                .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
                .get()

            val result = firebaseTask.await()

            result.forEach { r ->
                val folder = Folder(
                    r.id,
                    r.getString("email") ?: "",
                    r.getString("name") ?: "",
                    r.getString("description") ?: "",
                    r.getBoolean("isDelete") ?: false,
                )
                allFolder.add(folder)
            }
        } catch (ex: Exception) {
            Log.d("Exception getAllFolderOfUser MainActivity", ex.message ?: "")
        }
        return allFolder
    }

    private suspend fun getData() {

        val allFolder = getAllFolderOfUser();
        for (i in allFolder.indices) {
            val item = FolderVM(
                allFolder[i].id,
                allFolder[i].name, countTopicInFolder(allFolder[i].id!!),
                "",allFolder[i].email
            )
            folderList.add(item)
        }

        rcvFolderList.adapter = this.context?.let { FolderListAdapter(it,folderList) }

    }

    private suspend fun countTopicInFolder(folderId: String): Int {

        try {
            val querySnapshot: QuerySnapshot = db.collection("folder-topic")
                .whereEqualTo("folderId", folderId)
                .whereEqualTo("isDelete", false)
                .get()
                .await()

            return querySnapshot.size()
        } catch (e: Exception) {
            // Handle exceptions
            return -1
        }
    }

}