package com.example.english.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.english.R
import com.example.english.ViewModels.FolderVM
import com.example.english.ViewModels.TopicVM

class FolderListAdapter(private val dataList: ArrayList<FolderVM>) : RecyclerView.Adapter<FolderListAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.folder_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.tvHeading.text = currentItem.heading
        holder.tvCountTopic.text = currentItem.countTopics.toString() + " học phần"
        holder.ivImageUser.setImageResource(R.drawable.baseline_person_24)
        holder.tvEmailUser.text = currentItem.emailUser

        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, currentItem.heading, Toast.LENGTH_SHORT).show()
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvCountTopic: TextView = itemView.findViewById(R.id.tvCountTopic)
        val ivImageUser: ImageView = itemView.findViewById(R.id.ivImageUser)
        val tvEmailUser: TextView = itemView.findViewById(R.id.tvEmailUser)



    }

}