package ru.totowka.accountant.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.ramotion.foldingcell.FoldingCell
import kotlinx.coroutines.launch
import ru.totowka.accountant.R
import ru.totowka.accountant.Controller
import ru.totowka.accountant.backend.data.Product
import ru.totowka.accountant.backend.data.Transaction

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val controller = Controller()
    lateinit var mContent: TextView
    lateinit var mTransactions: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContent = findViewById(R.id.content)
        mTransactions = findViewById(R.id.transactions)
        mTransactions.layoutManager = LinearLayoutManager(this);

        findViewById<Button>(R.id.update_content).setOnClickListener(this)
        findViewById<Button>(R.id.get_content).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.get_content -> {
                lifecycleScope.launch {
                    mTransactions.adapter = TransactionAdapter(controller.getTransactions())
                }
            }
            R.id.update_content -> {
                val transaction = controller.scanQR(
                    "t=20210425T2100&s=238.38&fn=9282440300926607&i=12689&fp=3257250560&n=1"
                )
                controller.addTransaction(transaction)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}