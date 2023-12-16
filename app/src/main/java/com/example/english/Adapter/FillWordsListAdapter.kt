package com.example.english.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Models.FillWords
import com.example.english.Models.ObjectiveTest
import com.example.english.R

class FillWordsListAdapter(private val dataList: ArrayList<FillWords>) : RecyclerView.Adapter<FillWordsListAdapter.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fillwords_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.tvQuestion.text = currentItem.question
        holder.tvYourAnswer.text = currentItem.wordsFill
        holder.tvCorrectAnswer.text = currentItem.wordsCorrect

        holder.cvMain.setCardBackgroundColor(if (currentItem.equals())
            holder.itemView.context.getColor(R.color.green) else
            holder.itemView.context.getColor(R.color.redBlack))
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvMain: CardView = itemView.findViewById(R.id.cvMain)
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvYourAnswer: TextView = itemView.findViewById(R.id.tvYourAnswer)
        val tvCorrectAnswer: TextView = itemView.findViewById(R.id.tvCorrectAnswer)
    }
}