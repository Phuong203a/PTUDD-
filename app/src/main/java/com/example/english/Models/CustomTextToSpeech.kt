package com.example.english.Models

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.Locale


class CustomTextToSpeech(context: Context, private val word: String) {
    private var textToSpeech: TextToSpeech? = null

    init {
        initializeTextToSpeech(context)
        if (context == null) {
            Log.e("tag", "NGU")
        }
    }

    private fun initializeTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                // TextToSpeech initialization successful
                speak()
            }
        }
    }

    private fun speak() {
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Don't forget to release the TextToSpeech engine when it's no longer needed
    fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}