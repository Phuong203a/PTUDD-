package com.example.english.Util

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso

class Util {
    fun setAvatar(url: String?, imageView: ImageView, defaultId: Int) {
        try {
            if (url.isNullOrEmpty()) {
                imageView.setImageResource(defaultId)
                return
            }
            val uri = Uri.parse(url)
            Picasso.get()
                .load(uri)
                .fit()
                .into(imageView)
        } catch (ex: Exception) {
            imageView.setImageResource(defaultId)
        }
    }
}