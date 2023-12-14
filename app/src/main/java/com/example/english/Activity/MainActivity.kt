package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.english.Fragment.HomeFragment
import com.example.english.Fragment.LibraryFragment
import com.example.english.Fragment.ProfileFragment
import com.example.english.Fragment.SearchFragment
import com.example.english.R
import com.example.english.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: BottomSheetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
                startActivity(Intent(this, AddTopicActivity::class.java))
            }

            folderButton.setOnClickListener {
                startActivity(Intent(this, AddFolderActivity::class.java))
            }
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}