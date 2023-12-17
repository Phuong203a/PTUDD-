package com.example.english.Activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.english.Models.Flashcard
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FlashcardMovingActivity : AppCompatActivity() {
    private val topicCollection = FirebaseFirestore.getInstance().collection("topic")

    private lateinit var tvCurrentIndex: TextView
    private lateinit var cardFront: TextView
    private lateinit var cardBack: TextView
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button

    private lateinit var frontAnim: AnimatorSet
    private lateinit var backAnim: AnimatorSet
    private var isFront = true
    private var currentIndex = 0

    private val flashcardsList = ArrayList<Flashcard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_moving)

        val topicId = intent.getStringExtra("topicId")
        val isReverse = intent.getBooleanExtra("reverse", false)


        init()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (topicId != null) {
                    val querySnapshot = topicCollection.document(topicId).collection("vocabulary").get().await()

                    for (document in querySnapshot) {
                        val vocabulary = document.toObject(Vocabulary::class.java)
                        flashcardsList.add(if (isReverse) Flashcard(vocabulary.meaning, vocabulary.words)
                        else Flashcard(vocabulary.words, vocabulary.meaning))
                    }

                    flashcardsList.shuffle()
                    remplirData(true)
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }


        val scale: Float = applicationContext.resources.displayMetrics.density
        cardFront.cameraDistance = 8000 * scale
        cardBack.cameraDistance = 8000 * scale

        frontAnim = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animation) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animation) as AnimatorSet

        cardFront.setOnClickListener {
            if (isFront) {
                frontAnim.setTarget(cardFront)
                backAnim.setTarget(cardBack)
                frontAnim.start()
                backAnim.start()
                isFront = false
            } else {
                frontAnim.setTarget(cardBack)
                backAnim.setTarget(cardFront)
                frontAnim.start()
                backAnim.start()
                isFront = true
            }
        }

        btnPrevious.setOnClickListener {
            currentIndex--
            remplirData(false)
        }
    }

    private fun init() {
        tvCurrentIndex = findViewById(R.id.tvCurrentIndex)
        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun remplirData(isFirst: Boolean) {
        val current = flashcardsList[currentIndex]

        if(currentIndex == 0) {
            btnPrevious.visibility = View.GONE
        } else {
            btnPrevious.visibility = View.VISIBLE
        }

        if (currentIndex == flashcardsList.size - 1) {
            btnNext.text = "Hoàn thành"
            btnNext.setOnClickListener {
                Toast.makeText(this, "Hoàn thành", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            btnNext.text = "Tiếp"
            btnNext.setOnClickListener {
                currentIndex++
                remplirData(false)
            }
        }

        tvCurrentIndex.text = "${currentIndex + 1} / ${flashcardsList.size}"
        cardFront.text = current.frontWords
        cardBack.text = current.backWords

        if (!isFirst && !isFront) {
            frontAnim.start()
            backAnim.start()
            frontAnim.setTarget(cardBack)
            backAnim.setTarget(cardFront)
        }
        isFront = true
    }
}