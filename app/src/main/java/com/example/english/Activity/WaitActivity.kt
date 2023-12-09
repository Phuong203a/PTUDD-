package com.example.english.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.english.R
import java.util.Timer
import java.util.TimerTask

class WaitActivity : AppCompatActivity() {

    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@WaitActivity, LoginActivity::class.java))
            }
        }, 3000)
    }
}