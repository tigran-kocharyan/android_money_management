package ru.totowka.accountant

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.util.*

class MainActivity :  AppCompatActivity(), View.OnClickListener {

    val db = Firebase.firestore
    lateinit var mContent: TextView
    lateinit var mUpdateContent: Button
    lateinit var mGetContent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContent = findViewById(R.id.content)
        mUpdateContent = findViewById(R.id.update_content)
        mGetContent = findViewById(R.id.get_content)

        mUpdateContent.setOnClickListener(this)
        mGetContent.setOnClickListener(this)
    }

    fun getContent() {
        Log.d(TAG, "Clicked getContent()")
        val content = db.collection("purchase_info")
        val stringBuilder: StringBuilder = StringBuilder();
        content.get()
            .addOnSuccessListener { result ->
                run {
                    for (document in result) {
                        Log.d(TAG, "${document.id}=>${document.data}")
                        stringBuilder.appendln("${document.id} -> ${document.data}\n")
                    }
                    mContent.text = stringBuilder.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        Log.d(TAG, "The data from StringBuilder => $stringBuilder")
    }

    fun updateContent() {
        Log.d(TAG, "Clicked updateContent()")
        val docData = hashMapOf(
            "date" to Timestamp(Date()),
            "items" to hashMapOf(
                "amount" to 2,
                "price" to 100,
                "title" to "Pepsi"
            )
        )

        db.collection("purchase_info").document()
            .set(docData)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "Clicked onClickListener()")
        when(v?.id) {
            R.id.get_content -> getContent()
            R.id.update_content -> updateContent()
        }
    }


    companion object {
        const val TAG = "MainActivity"
    }

}