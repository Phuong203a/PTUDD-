package com.example.english.Models

import java.io.Serializable


data class ObjectiveTest(
    var question: String? = null,
    var answer1: String? = null,
    var answer2: String? = null,
    var answer3: String? = null,
    var answer4: String? = null,
    var correctIndex: Int? = null,
    var currentAnswer: Int? = null
) : Serializable {
}