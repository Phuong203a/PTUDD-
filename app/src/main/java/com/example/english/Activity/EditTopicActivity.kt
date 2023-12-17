package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.english.Models.FillWords
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditTopicActivity : AppCompatActivity() {
    private val topicCollection = FirebaseFirestore.getInstance().collection("topic")

    private lateinit var tvExit: TextView
    private lateinit var tvEdit: TextView
    private lateinit var edtTopic: EditText
    private lateinit var togglePublic: SwitchMaterial

    private var topic = Topic()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_topic)

        init()

        val topicId = intent.getStringExtra("topicId")
        if (topicId != null) {
            getTopic(topicId)
        }

        tvExit.setOnClickListener {
            finish()
        }

        tvEdit.setOnClickListener {
            handleEdit(topic)
        }
    }

    private fun init() {
        tvExit = findViewById(R.id.tvExit)
        tvEdit = findViewById(R.id.tvEdit)
        edtTopic = findViewById(R.id.edtTopic)
        togglePublic = findViewById(R.id.togglePublic)

        edtTopic.setText(topic.title)
        togglePublic.isChecked = topic.isPublic
    }

    private fun handleEdit(topic: Topic) {
        topic.title = edtTopic.text.toString()
        topic.isPublic = togglePublic.isChecked

        val updatedTopic = hashMapOf(
            "email" to topic.email.toString(),
            "title" to topic.title.toString(),
            "isPublic" to topic.isPublic,
            "isDelete" to topic.isDelete,
        )

        val topicDocument = topic.id?.let { topicCollection.document(it) }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                topicDocument?.update(updatedTopic as Map<String, Any>)?.await()

                finish()

            } catch (e: java.lang.Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun getTopic(topicId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (topicId != null) {
                    val querySnapshot = topicCollection.document(topicId).get().await()

                    topic = querySnapshot.toObject(Topic::class.java)!!
                    topic.id = topicId

                    edtTopic.setText(topic.title)
                    togglePublic.isChecked = topic.isPublic
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }
}