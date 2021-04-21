package ru.totowka.accountant.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.totowka.accountant.R
import ru.totowka.accountant.backend.FirebaseRepository
import ru.totowka.accountant.util.Product
import ru.totowka.accountant.util.Transaction
import java.lang.StringBuilder
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val fb = FirebaseRepository()
    lateinit var mContent: TextView
    lateinit var mUpdateContent: Button
    lateinit var mGetContent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContent = findViewById(R.id.content)
        findViewById<Button>(R.id.update_content).setOnClickListener(this)
        findViewById<Button>(R.id.get_content).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.get_content -> {
                val stringBuilder = StringBuilder();
                for (document in fb.getTransactions()) {
                    Log.d(TAG, "${document.id}=>${document.data}")
                    stringBuilder.appendln("${document.id} -> ${document.data}\n")
                }
                mContent.text = stringBuilder.toString()
            }
            R.id.update_content -> {
                val transaction = Transaction(
                    "qrinfo",
                    Timestamp.now(),
                    listOf(
                        Product(1, 1.0, "product_1"),
                        Product(2, 2.0, "product_2")
                    )
                )
                fb.addTransaction(transaction)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }

}