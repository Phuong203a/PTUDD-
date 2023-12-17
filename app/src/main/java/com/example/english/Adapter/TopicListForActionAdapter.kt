package com.example.english.Adapter
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Activity.AddNewTopicToFolderActivity
import com.example.english.Activity.FolderDetailActivity
import com.example.english.Activity.TopicVocabularyListActivity
import com.example.english.Models.Folder
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.google.firebase.firestore.FirebaseFirestore

class TopicListForActionAdapter(private val activity: Activity,
                                private val context: Context,
                                private val dataList: ArrayList<TopicVM>,
    private val folderId:String) :
    RecyclerView.Adapter<TopicListForActionAdapter.ViewHolderClass>() {
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
        holder.itemView.setOnClickListener {
            showConfirmAddDialog(currentItem.id!!)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvWordCount: TextView = itemView.findViewById(R.id.tvWordCount)
        val ivImageUser: ImageView = itemView.findViewById(R.id.ivImageUser)
        val tvEmailUser: TextView = itemView.findViewById(R.id.tvEmailUser)
    }
    private fun showConfirmAddDialog(topicId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Thêm vào folder")
        builder.setMessage("Bạn có muốn thêm vào folder không?")

        builder.setPositiveButton("Thêm") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            addToFolder(folderId, topicId)
        }

        builder.setNegativeButton("Huỷ") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun addToFolder(folderId:String,topicId:String){

        val newFolder = hashMapOf(
            "topicId" to topicId,
            "folderId" to folderId,
            "isDelete" to false
        )

        val docRef = FirebaseFirestore.getInstance().collection("folder-topic")
        docRef.add(newFolder)
            .addOnSuccessListener {
                Log.d(
                    "addToFolder",
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e -> Log.w("addToFolder", "Error writing document", e) }

        val intent = Intent(context, FolderDetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("folderId", folderId)
        activity.startActivity(intent)
        activity.finish()
    }
}