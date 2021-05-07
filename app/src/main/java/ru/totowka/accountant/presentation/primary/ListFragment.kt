package ru.totowka.accountant.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import ru.totowka.accountant.data.type.Transaction
import ru.totowka.accountant.presentation.adapter.TransactionAdapter
import ru.totowka.accountant.presentation.ui.AddTransactionActivity
import ru.totowka.accountant.presentation.ui.ScannerActivity

class ListFragment : Fragment(), View.OnClickListener {
    private val controller = Controller()
    lateinit var transactions: RecyclerView
    var transactions_list = ArrayList<TransactionAdapter.TransactionState>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        transactions = view.findViewById(R.id.transactions)
        transactions.layoutManager = LinearLayoutManager(requireActivity());
        transactions.adapter = TransactionAdapter(transactions_list)
        refresh()

        view.findViewById<FloatingActionButton>(R.id.add_transaction).setOnClickListener(this)
        view.findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            refresh()
            view.findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scanner -> {
                startActivityForResult(
                    Intent(requireActivity(), ScannerActivity::class.java),
                    REQUEST_QR
                )
                return true;
            }
            else -> {
                return false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_transaction -> {
                startActivityForResult(
                    Intent(requireActivity(), AddTransactionActivity::class.java),
                    REQUEST_TRANSACTION
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_QR -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra("qr")?.let {
                        controller.addTransaction(controller.scanQR(it))
                    }
                    refresh()
                } else {
                    Toast.makeText(requireActivity(), "QR Scanning Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            REQUEST_TRANSACTION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.getParcelableExtra<Transaction>("transaction")?.let {
                            addTransaction(it)
                        }
                        Toast.makeText(
                            requireActivity(), "Successfully added transaction!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Toast.makeText(
                        requireActivity(), "Adding transaction failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addTransaction(transaction: Transaction) {
        controller.addTransaction(transaction)
        transactions_list.add(TransactionAdapter.TransactionState(data = transaction))
        transactions.adapter?.notifyDataSetChanged()
    }


    private fun refresh() {
        lifecycleScope.launch {
            transactions_list.clear()
            transactions_list.addAll(controller.getTransactions().map {
                TransactionAdapter.TransactionState(data = it)
            } as ArrayList<TransactionAdapter.TransactionState>)
            transactions.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        const val REQUEST_QR = 1661
        const val REQUEST_TRANSACTION = 1662
    }
}