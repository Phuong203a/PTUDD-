package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.english.Fragment.HomeFragment
import com.example.english.Fragment.LibraryFragment
import com.example.english.Fragment.ProfileFragment
import com.example.english.Fragment.SearchFragment
import com.example.english.Models.Folder
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: BottomSheetDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()


        binding.bottomNavigationView.background = null

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_search -> replaceFragment(SearchFragment())
                R.id.navigation_add -> replaceFragment(HomeFragment())
                R.id.navigation_library -> replaceFragment(LibraryFragment())
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
            }
            true
        }

        binding.btnAdd.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottomsheet_layout, null)
            dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            dialog.show()

            val topicButton = dialogView.findViewById<CardView>(R.id.cvTopic)
            val folderButton = dialogView.findViewById<CardView>(R.id.cvFolder)

            topicButton.setOnClickListener {
                showCreateNewTopicDialog()
            }

            folderButton.setOnClickListener {
                showCreateNewFolderDialog();
            }
        }
    }



    private fun showCreateNewTopicDialog() {
        startActivity(Intent(this, AddTopicActivity::class.java))
        dialog.hide()
    }

    private fun showCreateNewFolderDialog() {
        startActivity(Intent(this, AddFolderActivity::class.java))
        dialog.hide()
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


    private fun createNewFolder(folder: Folder) {
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
            }
            .addOnFailureListener { e -> Log.w("createNewFolder", "Error writing document", e) }
    }
}