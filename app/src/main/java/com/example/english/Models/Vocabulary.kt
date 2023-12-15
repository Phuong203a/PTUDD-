package com.example.english.Models

import com.google.firebase.firestore.DocumentId

data class Vocabulary(
    val words: String? = null,
    val meaning: String? = null) {
    constructor(): this(null, null)
}