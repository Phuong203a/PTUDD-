package com.example.english.Adapter

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Activity.EditVocabularyActivity
import com.example.english.Models.CustomTextToSpeech
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.VocabularyVM
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class VocabularyListAdapter(private val dataList: ArrayList<VocabularyVM>,
                            private val mTTS: TextToSpeech,
                            private val isShow: Boolean
) : RecyclerView.Adapter<VocabularyListAdapter.ViewHolderClass>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.vocabulary_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        if(!isShow) {
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
            holder.ivSpeaker.visibility = View.GONE
        }
        val currentItem = dataList[position]
        holder.tvWords.text = currentItem.words
        holder.tvMeaning.text = currentItem.meaning

        holder.ivSpeaker.setOnClickListener{
            val textToSpeech = CustomTextToSpeech(holder.itemView.context, currentItem.words.toString())
//            textToSpeech.onDestroy()
        }

        holder.ivEdit.setOnClickListener{
            val intent = Intent(holder.itemView.context, EditVocabularyActivity::class.java)
            intent.putExtra("topicId", currentItem.idTopic)
            intent.putExtra("vocabularyId", currentItem.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.ivDelete.setOnClickListener{
            handleDelete(holder, currentItem)
        }
    }

    private fun handleDelete(holder: ViewHolderClass, vocabularyDeleted: VocabularyVM) {
        val builder = AlertDialog.Builder(holder.itemView.context)

        builder.setTitle("Xoá từ vựng")
        builder.setMessage("Bạn có muốn xoá từ vựng này không ?")

        var vocabulary = Vocabulary()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = vocabularyDeleted.idTopic?.let {
                    vocabularyDeleted.id?.let { it1 ->
                        db.collection("topic")
                            .document(it)
                            .collection("vocabulary")
                            .document(it1)
                            .get().await()
                    }
                }
                if (querySnapshot != null) {
                    vocabulary = querySnapshot.toObject(Vocabulary::class.java)!!
                }
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }



        // Positive button (Yes)
        builder.setPositiveButton("Xoá") { dialog: DialogInterface, _: Int ->
            vocabulary.isDelete = true
            val updatedVocabulary = hashMapOf(
                "words" to vocabulary.words.toString(),
                "meaning" to vocabulary.meaning.toString(),
                "isDelete" to vocabulary.isDelete,
            )

            val vocabularyDocument = vocabularyDeleted.idTopic?.let {
                vocabularyDeleted.id?.let { it1 ->
                    db.collection("topic")
                        .document(it)
                        .collection("vocabulary")
                        .document(it1)
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    vocabularyDocument?.update(updatedVocabulary as Map<String, Any>)?.await()

                } catch (e: Exception) {
                    Log.e("tag", e.toString())
                }
            }
            dialog.dismiss()
        }

        // Negative button (No)
        builder.setNegativeButton("Huỷ") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWords: TextView = itemView.findViewById(R.id.tvWords)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val ivSpeaker: ImageView = itemView.findViewById(R.id.ivSpeaker)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

        init {
            ivSpeaker.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTTS.speak(tvWords.text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }
}