package com.example.english.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Activity.TopicVocabularyListActivity
import com.example.english.R
import com.example.english.ViewModels.TopicVM

class TopicListAdapter(private val dataList: ArrayList<TopicVM>) : RecyclerView.Adapter<TopicListAdapter.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.topic_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.tvHeading.text = currentItem.title
        holder.tvWordCount.text = currentItem.countWords.toString() + " từ vựng"
        holder.ivImageUser.setImageResource(R.drawable.baseline_person_24)
        holder.tvEmailUser.text = currentItem.emailUser
        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, currentItem.title, Toast.LENGTH_SHORT).show()

            val intent = Intent(holder.itemView.context, TopicVocabularyListActivity::class.java)
            intent.putExtra("topicId", currentItem.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvWordCount: TextView = itemView.findViewById(R.id.tvWordCount)
        val ivImageUser: ImageView = itemView.findViewById(R.id.ivImageUser)
        val tvEmailUser: TextView = itemView.findViewById(R.id.tvEmailUser)
    }
}