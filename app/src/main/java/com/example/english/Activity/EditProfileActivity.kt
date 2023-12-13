package com.example.english.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.english.Fragment.ProfileFragment
import com.example.english.Models.User
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.ViewModels.UserVM
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date
import java.util.concurrent.FutureTask


class EditProfileActivity : AppCompatActivity() {
    private val SHARED_PREFS = "sharedPrefs"

    private lateinit var db: FirebaseFirestore
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private lateinit var txtEmail: TextView
    private lateinit var editTextUserName: EditText
    private lateinit var imageViewAvatar: ImageView
    private lateinit var btnUpdateProfile: Button
    private lateinit var user: User

    private lateinit var userViewModel: UserVM

    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        txtEmail = findViewById(R.id.txtEmailEditProfile)
        editTextUserName = findViewById(R.id.editTxtNameEditProfile)
        imageViewAvatar=findViewById(R.id.imgAvatarEditProfile)
        btnUpdateProfile = findViewById(R.id.btnSaveProfile)
        user = User()
        db = FirebaseFirestore.getInstance()

        val sharedPreferences  = this?.getSharedPreferences(SHARED_PREFS,
            MODE_PRIVATE
        )

        userViewModel = ViewModelProvider(this)[UserVM::class.java]


        val userEmail: String = sharedPreferences?.getString("email", null) ?: ""
        loadUser(userEmail)
        imageViewAvatar.setOnClickListener{selectImage()}
        btnUpdateProfile.setOnClickListener{updateUser(userEmail)}
    }

    private fun updateUser(email:String) {
        val docRef = db.collection("users").document(email)

        imageUri?.let {
            user.avatar = uploadImage(it)
        }
        docRef.update("name",editTextUserName.text.toString(),"avatar",user.avatar)
            .addOnSuccessListener { Log.d("updateUser", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener {  Log.w("updateUser", "Error updating document") }
        backToMain()
    }

    private fun loadUser(email:String){
        val docRef = db.collection("users").document(email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null ) {
                    // Retrieving values from Firestore document
                    user.name = document.getString("name") ?: ""
                    user.email = document.getString("email") ?: ""
                    user.avatar = document.getString("avatar") ?: ""

                    txtEmail.text= email
                    editTextUserName.setText(user.name)
                    Util().setAvatar(user.avatar,imageViewAvatar,R.drawable.default_avatar)

                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("loadUser", "get failed with ", exception)
            }
    }



    private fun uploadImage(imageUri: Uri?): String {
        var url = ""
        if (imageUri!=null) {
            try {
                val id = Date().time
                val ref = storageRef.child("avatar/${imageUri.lastPathSegment}_$id")
                val uploadTask = ref.putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    // Upload success
                }

                val futureTask = FutureTask {
                    try {
                        Tasks.await(uploadTask)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                Thread(futureTask).start()
                futureTask.get()

                val urlTask = FirebaseStorage.getInstance()
                    .getReference("avatar/${imageUri.lastPathSegment}_$id").downloadUrl

                urlTask.addOnSuccessListener {
                    // Download URL success
                }

                val futureTaskDownloadImage = FutureTask {
                    try {
                        Tasks.await(urlTask)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                Thread(futureTaskDownloadImage).start()

                val uri = futureTaskDownloadImage.get() as Uri?
                url = uri?.toString() ?: ""
            } catch (ex: Exception) {
                // Handle exception
            }
        }
        return url
    }
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500 && resultCode == RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                imageUri = selectedImageUri
                imageViewAvatar.setImageURI(imageUri)
            }
            }
        }
    private fun backToMain(){
//        val intent = Intent(this, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        startActivity(intent)
        val newUsername = user
        userViewModel.setUser(user)
        finish()
    }
}