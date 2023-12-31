package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.Models.User
import com.example.english.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtRePassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()

        btnSignUp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val username = edtName.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val rePassword = edtRePassword.text.toString().trim()

            handleSignUp(email, username, password, rePassword)
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        edtEmail = findViewById(R.id.edtEmail)
        edtName = findViewById(R.id.edtName)
        edtPassword = findViewById(R.id.edtPassword)
        edtRePassword = findViewById(R.id.edtRePassword)

        btnSignUp = findViewById(R.id.btnSignUp)

        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun handleSignUp(email: String, username: String, password: String, rePassword: String) {
        var isError = false

        if(email.isEmpty()) {
            edtEmail.error = "Email không được rỗng"
            isError = true
        }

        if(username.isEmpty()) {
            edtName.error = "Họ và tên không được rỗng"
            isError = true
        }

        if(password.isEmpty()) {
            edtPassword.error = "Mật khẩu không được rỗng"
            isError = true
        }

        if(password.isEmpty()) {
            edtPassword.error = "Mật khẩu phải ít nhất 6 chữ số"
            isError = true
        }

        if (isError)
            return

        if(password != rePassword) {
            Toast.makeText(this, "Mật khẩu không khớp với nhau", Toast.LENGTH_SHORT).show()
            return
        }

        val user: User = User(email, username, password)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                addUserFirebaseFirestore(user)

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                val error = it.exception?.message
                Log.e("Registration Error", error + "")
                Toast.makeText(this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addUserFirebaseFirestore(user: User) {
        val usersCollection = database.collection("users")

        val userDocument = user.email?.let { usersCollection.document(it) }

        if (userDocument != null) {
            userDocument.set(user)
                .addOnSuccessListener {
                    Log.d("Firestore", "Người dùng đã được thêm thành công!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Lỗi khi thêm người dùng", e)
                }
        }
    }
}