package com.example.english.Models

import com.google.firebase.firestore.PropertyName

data class Topic(
    val email: String? = null,
    val title: String? = null,
    @field:JvmField val isPublic: Boolean? = null,

    val vocabularyList: List<Vocabulary>?
) {
    constructor() : this(null, null, null, null)
}