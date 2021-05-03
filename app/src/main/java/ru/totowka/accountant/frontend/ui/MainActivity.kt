package ru.totowka.accountant.frontend.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.frontend.adapter.TransactionAdapter
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val controller = Controller()
    lateinit var mTransactions: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTransactions = findViewById(R.id.transactions)
        mTransactions.layoutManager = LinearLayoutManager(this);
        mTransactions.adapter = TransactionAdapter(emptyList())

        findViewById<FloatingActionButton>(R.id.read_qr).setOnClickListener(this)
        findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            refresh()
            findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.transaction_analytics -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
                refresh()
            } else {
                Toast.makeText(this, "QR Scanning Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            mTransactions.adapter =
                TransactionAdapter(
                    controller.getTransactions().map {
                        TransactionAdapter.TransactionState(data = it)
                    }
                )
        }
    }



    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_QR = 1661
    }
}