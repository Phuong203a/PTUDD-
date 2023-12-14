package com.example.english.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.english.Activity.ChangePasswordActivity
import com.example.english.Activity.EditProfileActivity
import com.example.english.Activity.LoginActivity
import com.example.english.Models.User
import com.example.english.R
import com.example.english.Util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private val SHARED_PREFS = "sharedPrefs"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var btnLogout: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnEditProfile: Button
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var cardView: CardView
    private lateinit var imageViewAvatar: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_profile, container, false)

        init(mView)

        loadUser()

        btnLogout.setOnClickListener {
            deleteLogin()

            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        btnEditProfile.setOnClickListener {
            requireActivity().run{
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }
        }

        btnChangePassword.setOnClickListener {
            requireActivity().run{
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }

        return mView
    }

    override fun onResume() {
        super.onResume()
        loadUser()
    }

    private fun init(mView: View) {
        btnLogout = mView.findViewById(R.id.btnLogout)
        btnChangePassword = mView.findViewById(R.id.btnChangePassword)
        btnEditProfile = mView.findViewById(R.id.btnEditProfile)
        txtEmail = mView.findViewById(R.id.txtEmailProfile)
        txtUserName = mView.findViewById(R.id.txtUserNameProfile)
        cardView = mView.findViewById(R.id.cardViewProfile)
        imageViewAvatar=mView.findViewById(R.id.imgAvatarProfile)
    }

    private fun deleteLogin() {
        val sharedPreferences = activity?.getSharedPreferences(SHARED_PREFS,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences?.edit()
        editor?.putString("email", "")
        editor?.apply()

        FirebaseAuth.getInstance().signOut()
    }

    private fun loadUser(){
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
                    user.name = document.getString("name") ?: ""
                    user.email = document.getString("email") ?: ""
                    user.avatar = document.getString("avatar") ?: ""

                    txtEmail.text = user.email;
                    txtUserName.text = user.name;
                    Util().setAvatar(user.avatar,imageViewAvatar,R.drawable.default_avatar)

                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("loadUser", "get failed with ", exception)
            }
    }
}