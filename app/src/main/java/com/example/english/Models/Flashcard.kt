package com.example.english.Models

class Flashcard {
    var frontWords: String? = null
    var backWords: String? = null

    constructor(frontWords: String?, backWords: String?) {
        this.frontWords = frontWords
        this.backWords = backWords
    }
}