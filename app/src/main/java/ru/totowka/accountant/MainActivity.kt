package ru.totowka.accountant

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder

class MainActivity :  AppCompatActivity() {

    val db = Firebase.firestore
    lateinit var content: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.content)
        val updateResult = updateContent()

        content.text = updateResult
    }

    fun updateContent() : String {
        val content = db.collection("purchase_info")
        val stringBuilder: StringBuilder = StringBuilder();
        content.get()
            .addOnSuccessListener { result ->
                for(document in result){
                    Log.d(TAG,"${document.id}=>${document.data}")
                    stringBuilder.append("${document.id} -> ${document.data}\n")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return stringBuilder.toString()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}