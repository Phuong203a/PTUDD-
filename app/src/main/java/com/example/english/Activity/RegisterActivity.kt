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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtRePassword: EditText

    private lateinit var btnSignUp: Button

    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edtEmail)
        edtName = findViewById(R.id.edtName)
        edtPassword = findViewById(R.id.edtPassword)
        edtRePassword = findViewById(R.id.edtRePassword)

        btnSignUp = findViewById(R.id.btnSignUp)

        tvLogin = findViewById(R.id.tvLogin)

        btnSignUp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val username = edtName.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val rePassword = edtRePassword.text.toString().trim()
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

            if (isError)
                return@setOnClickListener

            if(password != rePassword) {
                Toast.makeText(this, "Mật khẩu không khớp với nhau", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    val user: User = User(email, username, password)

                    database = FirebaseDatabase.getInstance().getReference("Users")
                    database.child(email).setValue(user).addOnSuccessListener {
                        Toast.makeText(this, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                    }


                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    val error = it.exception?.message
                    Log.e("Registration Error", error + "")
                    Toast.makeText(this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }


        tvLogin.setOnClickListener {
            finish()
        }
    }
}