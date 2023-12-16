package com.example.english.Models

import java.io.Serializable

class FillWords : Serializable {
    var question: String? = null
    var wordsCorrect: String? = null
    var wordsFill: String? = null

    constructor(question: String?, wordsCorrect: String?, wordsFill: String?) {
        this.question = question
        this.wordsCorrect = wordsCorrect
        this.wordsFill = wordsFill
    }


    fun equals(): Boolean {
        val wordsCorrectLowercase = wordsCorrect?.lowercase()?.trim()
        val wordsFillLowercase = wordsFill?.lowercase()?.trim()

        return wordsCorrectLowercase.equals(wordsFillLowercase)
    }
}