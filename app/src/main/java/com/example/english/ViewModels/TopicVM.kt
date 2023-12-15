package com.example.english.ViewModels

import com.example.english.Models.Vocabulary

data class TopicVM(
    var id: String? = null,
    var title: String? = null,
    var countWords: Int = 0,
    var emailUser: String? = null) {

}