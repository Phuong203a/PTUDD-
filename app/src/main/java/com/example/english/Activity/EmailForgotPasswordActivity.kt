package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.R
import com.google.firebase.auth.FirebaseAuth

class EmailForgotPasswordActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var edtEmail: EditText
    private lateinit var btnSend: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_forgot_password)

        init()

        btnSend.setOnClickListener {
            handleForgotPassword()
        }
    }

    private fun init() {
        edtEmail = findViewById(R.id.edtEmail)
        btnSend = findViewById(R.id.btnSend)
        progressBar = findViewById(R.id.forgetPasswordProgressbar)
    }

    private fun handleForgotPassword() {
        progressBar.visibility = View.VISIBLE;
        btnSend.visibility = View.INVISIBLE;
        compareEmail(edtEmail)
    }

    private fun compareEmail(email: EditText) {
        if(email.text.toString().isEmpty()) {
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }

        auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, "Đã gửi về email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}