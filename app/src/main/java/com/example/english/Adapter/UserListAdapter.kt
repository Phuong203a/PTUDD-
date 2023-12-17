package com.example.english.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Models.User
import com.example.english.R
import com.example.english.Util.Util
import com.example.english.ViewModels.FolderVM

class UserListAdapter(private val dataList: List<User>)
    : RecyclerView.Adapter<UserListAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return ViewHolderClass(itemView)
    }
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.txtName.text = currentItem.name
        holder.txtEmail.text = currentItem.email
        Util().setAvatar(currentItem.avatar, holder.ivImageUser,R.drawable.default_avatar)

    }
    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtUsernameItemUser)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmailItemUser)
        val ivImageUser: ImageView = itemView.findViewById(R.id.imgAvatarItemUser)
    }
}