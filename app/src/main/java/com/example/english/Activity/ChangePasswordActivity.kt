package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.english.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordActivity : AppCompatActivity() {
    private val SHARED_PREFS = "sharedPrefs"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var userEmail: String

    private lateinit var edtCurrentPassword: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var edtReturnNewPassword: EditText
    private lateinit var btnUpdatePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        init()

        btnUpdatePassword.setOnClickListener {
            val currentPassword = edtCurrentPassword.text.toString()
            val newPassword = edtNewPassword.text.toString()
            val returnNewPassword = edtReturnNewPassword.text.toString()

            handleChangePassword(currentPassword, newPassword, returnNewPassword)
        }

    }

    private fun init() {
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtReturnNewPassword = findViewById(R.id.edtReturnNewPassword)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)

        val sharedPreferences  = this?.getSharedPreferences(SHARED_PREFS,
            MODE_PRIVATE
        )
        userEmail = sharedPreferences?.getString("email", null) ?: ""
    }

    private fun handleChangePassword(currentPassword: String,
                                     newPassword: String,
                                     returnNewPassword: String) {


        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        if (newPassword != returnNewPassword) {
                            Toast.makeText(this, "Mật khẩu mới và nhập lại không khớp", Toast.LENGTH_SHORT).show()

                            return@addOnCompleteListener
                        }

                        user.updatePassword(newPassword)
                            .addOnCompleteListener { passwordUpdateTask ->
                                if (passwordUpdateTask.isSuccessful) {
                                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Mật khẩu hiện tại sai", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}