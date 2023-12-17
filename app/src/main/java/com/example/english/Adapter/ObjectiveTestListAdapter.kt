package com.example.english.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Models.ObjectiveTest
import com.example.english.R
import com.example.english.ViewModels.VocabularyVM

class ObjectiveTestListAdapter(private val dataList: ArrayList<ObjectiveTest>) : RecyclerView.Adapter<ObjectiveTestListAdapter.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.question_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.tvQuestion.text = currentItem.question
        holder.tvAnswer1.text = currentItem.answer1
        holder.tvAnswer2.text = currentItem.answer2
        holder.tvAnswer3.text = currentItem.answer3
        holder.tvAnswer4.text = currentItem.answer4

        val correctIndex = currentItem.correctIndex
        val chooseIndex = currentItem.currentAnswer

        if (correctIndex != null && chooseIndex != null) {
            setBackgroundAnswer(holder, correctIndex, chooseIndex)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvAnswer1: TextView = itemView.findViewById(R.id.tvAnswer1)
        val tvAnswer2: TextView = itemView.findViewById(R.id.tvAnswer2)
        val tvAnswer3: TextView = itemView.findViewById(R.id.tvAnswer3)
        val tvAnswer4: TextView = itemView.findViewById(R.id.tvAnswer4)
        val layoutAnswer1: LinearLayout = itemView.findViewById(R.id.layoutAnswer1)
        val layoutAnswer2: LinearLayout = itemView.findViewById(R.id.layoutAnswer2)
        val layoutAnswer3: LinearLayout = itemView.findViewById(R.id.layoutAnswer3)
        val layoutAnswer4: LinearLayout = itemView.findViewById(R.id.layoutAnswer4)

    }

    private fun setBackgroundAnswer(holder: ViewHolderClass, correctIndex: Int, chooseIndex: Int) {
        holder.layoutAnswer1.background = null
        holder.layoutAnswer2.background = null
        holder.layoutAnswer3.background = null
        holder.layoutAnswer4.background = null

        if (correctIndex != chooseIndex) {
            val redColor = Color.parseColor("#FF0000")
            when (chooseIndex) {
                1 -> holder.layoutAnswer1.setBackgroundColor(redColor)
                2 -> holder.layoutAnswer2.setBackgroundColor(redColor)
                3 -> holder.layoutAnswer3.setBackgroundColor(redColor)
                4 -> holder.layoutAnswer4.setBackgroundColor(redColor)
            }
        }

        val greenColor = Color.parseColor("#228B22")
        when (correctIndex) {
            1 -> holder.layoutAnswer1.setBackgroundColor(greenColor)
            2 -> holder.layoutAnswer2.setBackgroundColor(greenColor)
            3 -> holder.layoutAnswer3.setBackgroundColor(greenColor)
            4 -> holder.layoutAnswer4.setBackgroundColor(greenColor)
        }
    }
}