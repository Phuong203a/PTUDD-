package com.example.english.Activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Models.Folder
import com.example.english.Models.FolderTopic
import com.example.english.Models.Topic
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FolderDetailActivity : AppCompatActivity() {
    private lateinit var imageViewActionFolder: ImageView
    private lateinit var dialog: BottomSheetDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var txtFolderName: TextView
    private lateinit var txtFolderDescription: TextView
    private lateinit var folder: Folder
    private lateinit var rcvTopicListOfFolder: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)
        imageViewActionFolder = findViewById(R.id.imageViewActionFolder)
        txtFolderName = findViewById(R.id.txtFolderName)
        txtFolderDescription = findViewById(R.id.txtFolderDescription)
        rcvTopicListOfFolder= findViewById(R.id.rcvTopicListOfFolder)
        rcvTopicListOfFolder.layoutManager = LinearLayoutManager(this.applicationContext)
        rcvTopicListOfFolder.setHasFixedSize(true)

        db = FirebaseFirestore.getInstance()
        val folderId = intent.getStringExtra("folderId")
        lifecycleScope.launch(Dispatchers.Main) {
            folder = getFolder(folderId)!!
            getAllTopicOfFolder(folderId)
            txtFolderName.setText(folder?.name ?: "")
            txtFolderDescription.setText(folder?.description ?: "")
        }

        imageViewActionFolder.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottomshee_folder_layout, null)
            dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            dialog.show()

            val addTopicButton = dialogView.findViewById<CardView>(R.id.cvFolderAddTopicAction)
            val editFolderButton = dialogView.findViewById<CardView>(R.id.cvEditFolderAction)
            val deleteFolderButton = dialogView.findViewById<CardView>(R.id.cvFolderDeleteAction)
            deleteFolderButton.setOnClickListener {
                dialog.dismiss()
                showDeleteDialog(folder)
            }
            editFolderButton.setOnClickListener {
                dialog.dismiss()
                showUpdateDialog(folder)
            }
            addTopicButton.setOnClickListener {
                dialog.dismiss()
                addNewTopic(folder)
            }
        }
    }

    private fun addNewTopic(folder: Folder) {
        val intent = Intent(this, AddNewTopicToFolderActivity::class.java)
        intent.putExtra("folderId", folder.id)
        startActivity(intent)
        finish()
    }

    private fun updateFolder(folder: Folder) {
        val folderRef = db.collection("folder").document(folder.id!!)
        val updates = hashMapOf<String, Any>(
            "name" to folder.name.toString(),
            "description" to folder.description.toString(),
            "isDelete" to folder.isDelete,
            "email" to folder.email.toString(),

            )
        folderRef
            .update(updates)
            .addOnSuccessListener {
                Log.d(
                    "updateFolder",
                    "DocumentSnapshot successfully updated!"
                )

            }
            .addOnFailureListener { e -> Log.w("updateFolder", "Error updating document", e) }
    }

    private suspend fun getFolder(folderId: String?): Folder? {
        try {
            val firebaseTask = db.collection("folder")
                .document(folderId!!)
                .get()

            val r = firebaseTask.await()

            if (r.exists()) {
                return Folder(
                    folderId,
                    r.getString("email") ?: "",
                    r.getString("name") ?: "",
                    r.getString("description") ?: "",
                    r.getBoolean("isDelete") ?: false,
                )
            }
        } catch (ex: Exception) {
            Log.d("Exception getAllFolderOfUser MainActivity", ex.message ?: "")
        }
        return null
    }

    private suspend fun getAllTopicOfFolder(folderId: String?): List<Topic> {
        val allTopic = mutableListOf<Topic>()
        try {
            val firebaseTask = db.collection("folder-topic")
                .whereEqualTo("isDelete", false)
                .whereEqualTo("folderId", folderId)
                .get()

            val result = firebaseTask.await()
            var topicList: ArrayList<TopicVM> = arrayListOf()
            result.forEach { r ->
                val folderTopic = FolderTopic(
                    r.getString("folderId") ?: "",
                    r.getString("topicId") ?: "",
                    r.getBoolean("isDelete") ?: false,
                )
                val documentSnapshot  = db.collection("topic")
                    .document(folderTopic.topicId!!).get().await()
                if (documentSnapshot.exists()) {
                    val topicId = folderTopic.topicId
                    val topicVMNew = TopicVM()
                    val topic = documentSnapshot.toObject(Topic::class.java)
                    val vocabularySnapshot =
                        db.collection("topic").document(topicId!!)
                            .collection("vocabulary").get().await()

                    topicVMNew.countWords = vocabularySnapshot.size()
                    topicVMNew.title = topic?.title
                    topicVMNew.emailUser = topic?.email
                    topicVMNew.id = topicId
                    topicList.add(topicVMNew)
                }
            }
            rcvTopicListOfFolder.adapter = TopicListAdapter(topicList)
        } catch (ex: Exception) {
            Log.d("Exception getAllTopicByName", ex.message ?: "")
        }
        return allTopic
    }

    private fun showDeleteDialog(folder: Folder) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Xoá Folder")
        builder.setMessage("Bạn có muốn xoá folder này không ?")

        // Positive button (Yes)
        builder.setPositiveButton("Xoá") { dialog: DialogInterface, _: Int ->
            folder.isDelete = true
            updateFolder(folder)
            dialog.dismiss()
            backToMain()
        }

        // Negative button (No)
        builder.setNegativeButton("Huỷ") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showUpdateDialog(folder: Folder) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_folder_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextFolderName = dialogView.findViewById<EditText>(R.id.editTextFolderNameEdit)
        val editTextFolderDescription = dialogView.findViewById<EditText>(R.id.editTextFolderDescriptionEdit)
        val btnOk = dialogView.findViewById<Button>(R.id.btnEditFolder)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelEditolder)
        editTextFolderName.setText(folder.name)
        editTextFolderDescription.setText(folder.description)

        val dialog = dialogBuilder.create()
        dialog.show()

        btnOk.setOnClickListener {
            folder.name=editTextFolderName.text.toString()
            folder.description=editTextFolderDescription.text.toString()
            if(folder.name.isNullOrEmpty() || folder.description.isNullOrEmpty()){
                Toast.makeText(this, "Hãy điền đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateFolder(folder)
            dialog.dismiss()
            backToMain()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun backToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}