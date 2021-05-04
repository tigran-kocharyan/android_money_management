package ru.totowka.accountant.frontend.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.frontend.adapter.TransactionAdapter

class TransactionsFragment : Fragment(), View.OnClickListener {
    private val controller = Controller()
    lateinit var mTransactions: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        mTransactions = view.findViewById(R.id.transactions)
        mTransactions.layoutManager = LinearLayoutManager(requireActivity());
        mTransactions.adapter = TransactionAdapter(emptyList())

        view.findViewById<FloatingActionButton>(R.id.read_qr).setOnClickListener(this)
        view.findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            refresh()
            view.findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }

        return view
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.read_qr -> {
                startActivityForResult(
                    Intent(requireActivity(), ScannerActivity::class.java),
                    MainActivity.REQUEST_QR
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            MainActivity.REQUEST_QR -> if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra("qr")?.let {
                    controller.addTransaction(controller.scanQR(it))
                }
                refresh()
            } else {
                Toast.makeText(requireActivity(), "QR Scanning Failed", Toast.LENGTH_SHORT).show()
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
}