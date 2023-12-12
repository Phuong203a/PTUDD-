package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.Models.Folder
import com.example.english.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddFolderActivity : AppCompatActivity() {
    private lateinit var editTextFolderNameCreate: EditText
    private lateinit var editTextFolderDescription: EditText
    private lateinit var textCancelNewFolder: TextView
    private lateinit var textCreateNewFolder: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_folder)
        editTextFolderNameCreate = findViewById(R.id.editTextFolderNameCreate)
        editTextFolderDescription = findViewById(R.id.editTextFolderDescriptionCreate)
        textCancelNewFolder = findViewById(R.id.textCancelNewFolder)
        textCreateNewFolder = findViewById(R.id.textCreateNewFolder)
        db = FirebaseFirestore.getInstance()

        textCreateNewFolder.setOnClickListener {
            createNewFolder()
        }
        textCancelNewFolder.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createNewFolder() {
        val folderName = editTextFolderNameCreate.text.toString()
        val folderDescription = editTextFolderDescription.text.toString()
        if (folderName.isNullOrEmpty() || folderDescription.isNullOrEmpty()) {
            Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        var folder = Folder(
            FirebaseAuth.getInstance().currentUser?.email,
            folderName, folderDescription, false
        )
        saveNewFolder(folder)
    }

    private fun saveNewFolder(folder: Folder) {
        val newFolder = hashMapOf(
            "email" to folder.email,
            "name" to folder.name,
            "description" to folder.description,
            "isDelete" to false
        )

        val docRef = db.collection("folder")
        docRef.add(newFolder)
            .addOnSuccessListener {
                Log.d(
                    "createNewFolder",
                    "DocumentSnapshot successfully written!"
                )
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e -> Log.w("createNewFolder", "Error writing document", e) }
    }
}