package com.example.english.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.english.R
import com.google.android.material.switchmaterial.SwitchMaterial

class ObjectiveTestActivity : AppCompatActivity() {
    private lateinit var toggleContrary: SwitchMaterial
    private lateinit var toggleFeedback: SwitchMaterial
    private lateinit var btnStart: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objective_test)

        val topicId = intent.getStringExtra("topicId")

        init()

        btnStart.setOnClickListener {
            val intent = Intent(this, ObjectiveTestQuestionActivity::class.java)
            intent.putExtra("topicId", topicId)
            intent.putExtra("reverse", toggleContrary.isChecked)
            intent.putExtra("feedback", toggleFeedback.isChecked)
            startActivity(intent)
            finish()
        }
    }

    private fun init() {
        toggleContrary = findViewById(R.id.toggleContrary)
        toggleFeedback = findViewById(R.id.toggleFeedback)
        btnStart = findViewById(R.id.btnStart)
    }
}