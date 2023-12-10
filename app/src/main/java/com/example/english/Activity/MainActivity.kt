package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.english.Fragment.HomeFragment
import com.example.english.Fragment.LibraryFragment
import com.example.english.Fragment.ProfileFragment
import com.example.english.Fragment.SearchFragment
import com.example.english.R
import com.example.english.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment: HomeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_search -> replaceFragment(SearchFragment())
                R.id.navigation_add -> replaceFragment(HomeFragment())
                R.id.navigation_library -> replaceFragment(LibraryFragment())
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}