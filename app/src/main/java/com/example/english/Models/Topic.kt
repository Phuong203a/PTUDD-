package com.example.english.Models

import com.google.firebase.firestore.PropertyName

data class Topic(
    var id: String? = null,
    var email: String? = null,
    var title: String? = null,
    @field:JvmField var isPublic: Boolean,
    var isDelete: Boolean = false
) {
    constructor() : this(null, null, null, false, false)
}