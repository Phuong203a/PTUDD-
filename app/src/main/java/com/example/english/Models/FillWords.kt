package com.example.english.Models
class FillWords {
    var wordsCorrect: String? = null
    var wordsFill: String? = null

    fun equals(): Boolean {
        val wordsCorrectLowercase = wordsCorrect?.lowercase()?.trim()
        val wordsFillLowercase = wordsFill?.lowercase()?.trim()

        return wordsCorrectLowercase.equals(wordsFillLowercase)
    }
}