package com.example.english.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Activity.FolderDetailActivity
import com.example.english.R
import com.example.english.ViewModels.FolderVM
import com.example.english.ViewModels.TopicVM
import kotlinx.coroutines.CoroutineScope

class FolderListAdapter(private val context: Context, private val dataList: ArrayList<FolderVM>) : RecyclerView.Adapter<FolderListAdapter.ViewHolderClass>() {

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
            val intent = Intent(context, FolderDetailActivity::class.java)
            intent.putExtra("folderId", currentItem.folderId)
            context.startActivity(intent)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvCountTopic: TextView = itemView.findViewById(R.id.tvCountTopic)
        val ivImageUser: ImageView = itemView.findViewById(R.id.ivImageUser)
        val tvEmailUser: TextView = itemView.findViewById(R.id.tvEmailUser)



    }

}