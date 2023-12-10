package com.example.english.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.english.Activity.LoginActivity
import com.example.english.R

class ProfileFragment : Fragment() {
    private lateinit var btnLogout: Button
    private val SHARED_PREFS = "sharedPrefs"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_profile, container, false)

        btnLogout = mView.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            deleteLogin()

            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        return mView
    }

    private fun deleteLogin() {
        val sharedPreferences = activity?.getSharedPreferences(SHARED_PREFS,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences?.edit()
        editor?.putString("email", "")
        editor?.apply()
    }
}