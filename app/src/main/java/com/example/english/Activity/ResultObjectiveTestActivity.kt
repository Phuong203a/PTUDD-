package com.example.english.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.ObjectiveTestListAdapter
import com.example.english.Adapter.VocabularyListAdapter
import com.example.english.Models.ObjectiveTest
import com.example.english.R
import com.example.english.ViewModels.VocabularyVM

class ResultObjectiveTestActivity : AppCompatActivity() {
    private lateinit var tvCorrect: TextView
    private lateinit var btnContinue: Button
    private lateinit var rcvDetailAnswer: RecyclerView

    private var score: Int = 0
    private var questionList: ArrayList<ObjectiveTest> = arrayListOf<ObjectiveTest>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_objective_test)

        score = intent.getIntExtra("score", 0)
        val receivedBundle = intent.extras
        val receivedList = receivedBundle?.getSerializable("questionList") as ArrayList<ObjectiveTest>?

        if (receivedList != null) {
            questionList = receivedList.toCollection(ArrayList())
        }
        init()

        tvCorrect.text = "${score} / ${questionList.size}"

        rcvDetailAnswer.layoutManager = LinearLayoutManager(this)
        rcvDetailAnswer.setHasFixedSize(true)
        rcvDetailAnswer.adapter = ObjectiveTestListAdapter(questionList)

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