package com.example.english.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.english.R
import com.example.english.ViewModels.VocabularyVM

class VocabularyListAdapter(private val dataList: ArrayList<VocabularyVM>) : RecyclerView.Adapter<VocabularyListAdapter.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.vocabulary_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.tvWords.text = currentItem.words
        holder.tvMeaning.text = currentItem.meaning

        holder.ivSpeaker.setOnClickListener{
            Toast.makeText(holder.itemView.context, "Speaker ${currentItem.words}", Toast.LENGTH_SHORT).show()
        }

        holder.ivEdit.setOnClickListener{
            Toast.makeText(holder.itemView.context, "Edit ${currentItem.words}", Toast.LENGTH_SHORT).show()
        }

        holder.ivDelete.setOnClickListener{
            Toast.makeText(holder.itemView.context, "Delete ${currentItem.words}", Toast.LENGTH_SHORT).show()
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWords: TextView = itemView.findViewById(R.id.tvWords)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val ivSpeaker: ImageView = itemView.findViewById(R.id.ivSpeaker)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }
}