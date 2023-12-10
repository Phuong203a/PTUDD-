package com.example.english.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.english.Activity.LoginActivity
import com.example.english.Models.User
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var btnLogout: Button
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private val SHARED_PREFS = "sharedPrefs"
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_profile, container, false)
        db = FirebaseFirestore.getInstance()
        val user = loadUser()

        btnLogout = mView.findViewById(R.id.btnLogout)
        txtEmail = mView.findViewById(R.id.txtEmailProfile)
        txtUserName = mView.findViewById(R.id.txtUserNameProfile)



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
    private fun loadUser() :User{
        val sharedPreferences  = activity?.getSharedPreferences(SHARED_PREFS,
            AppCompatActivity.MODE_PRIVATE
        )

        val user = User()
        val email: String? = sharedPreferences?.getString("email", null)
        val userEmail: String = email ?: ""

        val docRef = db.collection("users").document(userEmail)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null ) {
                    // Retrieving values from Firestore document
                    user.name = document.getString("name") ?: ""
                    user.email = document.getString("email") ?: ""

                    txtEmail.text = user.email;
                    txtUserName.text = user.name;
                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("loadUser", "get failed with ", exception)
            }
        return user
    }
}