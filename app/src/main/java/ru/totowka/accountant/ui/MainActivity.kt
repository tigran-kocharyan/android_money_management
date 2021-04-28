package ru.totowka.accountant.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.totowka.accountant.R
import ru.totowka.accountant.Controller

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

        findViewById<FloatingActionButton>(R.id.read_qr).setOnClickListener(this)
        findViewById<Button>(R.id.get_content).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.get_content -> {
                lifecycleScope.launch {
                    mTransactions.adapter = TransactionAdapter(controller.getTransactions())
                }
            }
            R.id.read_qr -> {
                startActivityForResult(
                    Intent(this, ScannerActivity::class.java),
                    REQUEST_QR
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_QR -> if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra("qr")?.let {
                    controller.addTransaction(controller.scanQR(it))
                }
            } else {
                Toast.makeText(this, "QR Scanning Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_QR = 1661
    }
}