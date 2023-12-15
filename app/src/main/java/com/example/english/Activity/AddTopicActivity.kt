package com.example.english.Activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AddTopicActivity : AppCompatActivity() {
    private val SHARED_PREFS = "sharedPrefs"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var tvExit: TextView
    private lateinit var tvAdd: TextView
    private lateinit var edtTopic: EditText
    private lateinit var togglePublic: SwitchMaterial

    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)

        init()

        tvExit.setOnClickListener {
            finish()
        }

        tvAdd.setOnClickListener {
            handleAdd()
        }

    }

    private fun init() {
        tvExit = findViewById(R.id.tvExit)
        tvAdd = findViewById(R.id.tvAdd)
        edtTopic = findViewById(R.id.edtTopic)
        togglePublic = findViewById(R.id.togglePublic)

        val sharedPreferences  = this.getSharedPreferences(SHARED_PREFS,
            MODE_PRIVATE
        )
        userEmail = sharedPreferences?.getString("email", null) ?: ""
    }

    private fun handleAdd() {
        val topic = Topic(userEmail, edtTopic.text.toString(), togglePublic.isChecked )

        val newTopic = hashMapOf(
            "email" to topic.email,
            "title" to topic.title,
            "isPublic" to topic.isPublic,
        )
        val topicCollection = db.collection("topic")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val topicDocumentNew = topicCollection.add(newTopic).await()

                Toast.makeText(applicationContext, "Thêm topic thành công", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Log.e("tag", e.toString())
                Toast.makeText(applicationContext, "Thêm topic thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}