package com.example.english.Activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.english.Adapter.TopicListAdapter
import com.example.english.Adapter.VocabularyListAdapter
import com.example.english.Models.Folder
import com.example.english.Models.Topic
import com.example.english.Models.Vocabulary
import com.example.english.R
import com.example.english.ViewModels.TopicVM
import com.example.english.ViewModels.VocabularyVM
import com.google.firebase.firestore.FirebaseFirestore
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.Locale

class TopicVocabularyListActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var tvTitle: TextView
    private lateinit var tvMode: TextView
    private lateinit var ivEdit: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var rcvVocabularyList: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var cvFlashcard: CardView
    private lateinit var cvObjectiveTest: CardView
    private lateinit var cvFillWords: CardView
    private lateinit var ivImportCsv: ImageView
    private lateinit var ivExportCsv: ImageView

    private var vocabularyList: ArrayList<VocabularyVM> = arrayListOf<VocabularyVM>()
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var topicId: String
    private var isStart = false

    private val pickCsvFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    readCsvData(inputStream)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_vocabulary_list)

        topicId = intent.getStringExtra("topicId").toString()

        init()
        isStart = true
        Log.e("tag", "onCreate")


        cvFlashcard.setOnClickListener {
            val intent = Intent(this, FlashcardSettingActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        cvObjectiveTest.setOnClickListener {
            val intent = Intent(this, ObjectiveTestActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        cvFillWords.setOnClickListener {
            val intent = Intent(this, FillWordsActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        ivEdit.setOnClickListener {
            val intent = Intent(this, EditTopicActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        ivDelete.setOnClickListener {
            if (topicId != null) {
                handleDelete(topicId)
            }
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddVocabularyActivity::class.java)
            intent.putExtra("topicId", topicId)
            startActivity(intent)
        }

        ivImportCsv.setOnClickListener {
            openFilePicker()
        }

        ivExportCsv.setOnClickListener {
            exportCsvData()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("tag", "onResume")
        if (!isStart) {
            init()
        } else {
            isStart = false
        }
    }

    private fun init() {
        tvTitle = findViewById(R.id.tvTitle)
        tvMode = findViewById(R.id.tvMode)
        ivEdit = findViewById(R.id.ivEdit)
        ivDelete = findViewById(R.id.ivDelete)
        rcvVocabularyList = findViewById(R.id.rcvVocabularyList)
        btnAdd = findViewById(R.id.btnAdd)
        cvFlashcard = findViewById(R.id.cvFlashcard)
        cvObjectiveTest = findViewById(R.id.cvObjectiveTest)
        cvFillWords = findViewById(R.id.cvFillWords)
        ivImportCsv = findViewById(R.id.ivImportCsv)
        ivExportCsv = findViewById(R.id.ivExportCsv)

        rcvVocabularyList.layoutManager = LinearLayoutManager(this)
        rcvVocabularyList.setHasFixedSize(true)

        textToSpeech = TextToSpeech(this) {
            Log.i("TextToSpeech", "onCreate: $it")
        }
        textToSpeech.setLanguage(Locale.ENGLISH)

        if (topicId != null) {
            showData(topicId)
        }
    }

    private fun showData(topicId: String) {
        showDataTopic(topicId)
        showDataVocabulary(topicId)
    }

    private fun showDataTopic(topicId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic").document(topicId).get().await()
                val topic = querySnapshot.toObject(Topic::class.java)

                if (topic != null) {
                    tvTitle.text = topic.title
                    tvMode.text = if (topic.isPublic == true) "Công khai" else "Riêng tư"
                }

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun showDataVocabulary(topicId: String) {
        (rcvVocabularyList.adapter as? VocabularyListAdapter)?.clearData()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic")
                    .document(topicId)
                    .collection("vocabulary")
                    .whereEqualTo("isDelete", false)
                    .get().await()

                for (document in querySnapshot) {
                    val vocabularyVMNew = VocabularyVM()

                    val vocabulary = document.toObject(Vocabulary::class.java)

                    vocabularyVMNew.id = document.id
                    vocabularyVMNew.idTopic = topicId
                    vocabularyVMNew.words = vocabulary.words
                    vocabularyVMNew.meaning = vocabulary.meaning

                    vocabularyList.add(vocabularyVMNew)
                }

                    rcvVocabularyList.adapter = VocabularyListAdapter(vocabularyList, textToSpeech)
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun updateTopic(topic: Topic) {

        val updatedTopic = hashMapOf(
            "email" to topic.email.toString(),
            "title" to topic.title.toString(),
            "isPublic" to topic.isPublic,
            "isDelete" to topic.isDelete,
        )

        val topicDocument = topic.id?.let { db.collection("topic").document(it) }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                topicDocument?.update(updatedTopic as Map<String, Any>)?.await()

                finish()

            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }
    }

    private fun handleDelete(topicId: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Xoá Topic")
        builder.setMessage("Bạn có muốn xoá topic này không ?")

        var topic = Topic()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("topic").document(topicId).get().await()
                topic = querySnapshot.toObject(Topic::class.java)!!
            } catch (e: Exception) {
                Log.e("tag", e.toString())
            }
        }



        // Positive button (Yes)
        builder.setPositiveButton("Xoá") { dialog: DialogInterface, _: Int ->
            topic.isDelete = true
            topic.id = topicId
            val updatedTopic = hashMapOf(
                "email" to topic.email.toString(),
                "title" to topic.title.toString(),
                "isPublic" to topic.isPublic,
                "isDelete" to topic.isDelete,
            )

            val topicDocument = topic.id?.let { db.collection("topic").document(it) }

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    topicDocument?.update(updatedTopic as Map<String, Any>)?.await()
                    showData(topicId)
                    dialog.dismiss()
                    finish()

                } catch (e: Exception) {
                    Log.e("tag", e.toString())
                }
            }

        }

        // Negative button (No)
        builder.setNegativeButton("Huỷ") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        pickCsvFile.launch(intent)
    }

    private fun readCsvData(inputStream: java.io.InputStream?) {
        try {
            inputStream?.let {
                val reader = BufferedReader(InputStreamReader(it))
                val csvReader = CSVReader(reader)

                var nextLine: Array<String>?
                while (csvReader.readNext().also { nextLine = it } != null) {
                    // Process each row
                    val vocabulary = Vocabulary()
                    var i = 0

                    for (column in nextLine!!) {
                        if (i % 2 == 0) {
                            vocabulary.words = column
                        } else {
                            vocabulary.meaning = column
                        }
                        i++
                    }

                    val newVocabulary = hashMapOf(
                        "words" to vocabulary.words,
                        "meaning" to vocabulary.meaning,
                        "isDelete" to vocabulary.isDelete
                    )

                    val vocabularyCollection = topicId?.let { db.collection("topic")
                        .document(it)
                        .collection("vocabulary") }

                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            val vocabularyDocumentNew = vocabularyCollection?.add(newVocabulary)?.await()

                            Toast.makeText(applicationContext, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show()
                            finish()

                        } catch (e: Exception) {
                            Log.e("tag", e.toString())
                            Toast.makeText(applicationContext, "Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    showData(topicId)
                    reader.close()
                }, 10000)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun exportCsvData() {
        val csvFileName = "exported_data.csv"

        try {
            // Get the "Downloads" directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Create the CSV file in the "Downloads" directory
            val file = File(downloadsDir, csvFileName)
            val writer = CSVWriter(FileWriter(file))

            // Write header
            val header = arrayOf("words", "meaning")
            writer.writeNext(header)

            // Iterate through your data and write each row to the CSV file
            for (vocabulary in vocabularyList) {
                val row = arrayOf(vocabulary.words, vocabulary.meaning)
                writer.writeNext(row)
            }

            // Close the writer
            writer.close()

            // Notify the system about the new file
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                null
            ) { _, _ ->
                // Scanning completed
            }

            // Inform the user that the export was successful
            Toast.makeText(this, "CSV Exported Successfully", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            e.printStackTrace()

            // Inform the user that an error occurred during export
            Toast.makeText(this, "Error Exporting CSV", Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            // Set the language for TextToSpeech (you can modify this accordingly)
//            val result = textToSpeech.setLanguage(Locale.ENGLISH)
//            textToSpeech.setPitch(1F);
//
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TextToSpeech", "Language is not supported or missing data")
//            }
//        } else {
//            Log.e("TextToSpeech", "Initialization failed")
//        }
//    }
//    override fun onDestroy() {
//        if (::textToSpeech.isInitialized) {
//            textToSpeech.stop()
//            textToSpeech.shutdown()
//        }
//        super.onDestroy()
//    }

}