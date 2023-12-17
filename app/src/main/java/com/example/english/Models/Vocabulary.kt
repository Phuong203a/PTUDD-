package com.example.english.Models

import com.google.firebase.firestore.DocumentId

data class Vocabulary(
    var words: String? = null,
    var meaning: String? = null,
    var isDelete: Boolean = false) {
    constructor(): this(null, null)
}