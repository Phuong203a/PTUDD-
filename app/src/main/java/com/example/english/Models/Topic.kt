package com.example.english.Models

data class Topic(
    var id: String? = null,
    var title: String? = null,
    var isPublic: Boolean? = null,
    var email: String? = null,
    var listVocabulary: List<Vocabulary>
)