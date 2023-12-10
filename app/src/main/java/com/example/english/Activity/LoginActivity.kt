package com.example.english.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var edtEmail:EditText
    private lateinit var edtPassword:EditText
    private lateinit var btnLogin:Button

    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUp:TextView

    private val SHARED_PREFS = "sharedPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkLogined()

        auth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)

        btnLogin = findViewById(R.id.btnLogin)

        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSignUp = findViewById(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if(!password.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {

                            saveLogin(email)

                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    edtPassword.error = "Mật khẩu không được rỗng"
                }
            } else if (email.isEmpty()) {
                edtEmail.error = "Email không được rỗng"
            } else {
                edtEmail.error = "Nhập Email tồn tại"
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, EmailForgotPasswordActivity::class.java))
            finish()
        }

    }

    private fun saveLogin(email: String) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

    private fun checkLogined() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        val email = sharedPreferences.getString("email", "")

        if (email!!.isEmpty()) return

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}