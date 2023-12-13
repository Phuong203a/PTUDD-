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
import com.example.english.Activity.EditProfileActivity
import com.example.english.Activity.LoginActivity
import com.example.english.Models.User
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.ViewModels.UserVM
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var userViewModel: UserVM
    private lateinit var btnLogout: Button
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var cardView: CardView
    private lateinit var imageViewAvatar: ImageView
    private val SHARED_PREFS = "sharedPrefs"
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_profile, container, false)
        db = FirebaseFirestore.getInstance()
        loadUser()

        btnLogout = mView.findViewById(R.id.btnLogout)
        txtEmail = mView.findViewById(R.id.txtEmailProfile)
        txtUserName = mView.findViewById(R.id.txtUserNameProfile)
        cardView = mView.findViewById(R.id.cardViewProfile)
        imageViewAvatar=mView.findViewById(R.id.imgAvatarProfile)

        Log.e("tag", "Hello")

        btnLogout.setOnClickListener {
            deleteLogin()

            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        cardView.setOnClickListener{
            requireActivity().run{
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
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

                    operationViewModel(user)
                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("loadUser", "get failed with ", exception)
            }
    }

    private fun operationViewModel(user: User) {
        userViewModel = ViewModelProvider(requireActivity())[UserVM::class.java]


        userViewModel.setUser(user)

        userViewModel.userData.observe(viewLifecycleOwner) { user -> loadUser() }

//        userViewModel.email.observe(this, { email ->
//            // Xử lý khi email thay đổi
//        })
//
//        userViewModel.name.observe(this, { name ->
//            // Xử lý khi name thay đổi
//        })
//
//        userViewModel.password.observe(this, { password ->
//            // Xử lý khi password thay đổi
//        })
//
//        userViewModel.avatar.observe(this, { avatar ->
//            // Xử lý khi avatar thay đổi
//        })
    }
}