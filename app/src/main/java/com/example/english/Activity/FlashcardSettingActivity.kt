package com.example.english.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.english.R
import com.google.android.material.switchmaterial.SwitchMaterial

class FlashcardSettingActivity : AppCompatActivity() {
    private lateinit var toggleContrary: SwitchMaterial
    private lateinit var btnStart: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_setting)
        val topicId = intent.getStringExtra("topicId")

        init()

        btnStart.setOnClickListener {
            val intent = Intent(this, FlashcardMovingActivity::class.java)
            intent.putExtra("topicId", topicId)
            intent.putExtra("reverse", toggleContrary.isChecked)
            startActivity(intent)
            finish()
        }
    }

    private fun init() {
        toggleContrary = findViewById(R.id.toggleContrary)
        btnStart = findViewById(R.id.btnStart)
    }
}