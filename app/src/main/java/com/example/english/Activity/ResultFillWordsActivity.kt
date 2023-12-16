package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.FillWordsListAdapter
import com.example.english.Adapter.ObjectiveTestListAdapter
import com.example.english.Models.FillWords
import com.example.english.Models.ObjectiveTest
import com.example.english.R

class ResultFillWordsActivity : AppCompatActivity() {
    private lateinit var tvCorrect: TextView
    private lateinit var btnContinue: Button
    private lateinit var rcvDetailAnswer: RecyclerView

    private var score: Int = 0
    private var fillWordsList: ArrayList<FillWords> = arrayListOf<FillWords>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_fill_words)

        score = intent.getIntExtra("score", 0)
        val receivedBundle = intent.extras
        val receivedList = receivedBundle?.getSerializable("fillWordsList") as ArrayList<FillWords>?

        if (receivedList != null) {
            fillWordsList = receivedList.toCollection(ArrayList())
        }
        init()

        tvCorrect.text = "${score} / ${fillWordsList.size}"

        rcvDetailAnswer.layoutManager = LinearLayoutManager(this)
        rcvDetailAnswer.setHasFixedSize(true)
        rcvDetailAnswer.adapter = FillWordsListAdapter(fillWordsList)

        btnContinue.setOnClickListener {
            finish()
        }
    }
    private fun init() {
        tvCorrect = findViewById(R.id.tvCorrect)
        btnContinue = findViewById(R.id.btnContinue)
        rcvDetailAnswer = findViewById(R.id.rcvDetailAnswer)
    }

}