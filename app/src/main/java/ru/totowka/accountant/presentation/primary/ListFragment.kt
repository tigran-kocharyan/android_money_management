package ru.totowka.accountant.presentation.primary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.data.TimeFilter
import ru.totowka.accountant.data.type.Transaction
import ru.totowka.accountant.presentation.adapter.TransactionAdapter
import ru.totowka.accountant.presentation.secondary.ScannerActivity
import ru.totowka.accountant.presentation.secondary.insert.AddTransactionActivity

class ListFragment : Fragment(), View.OnClickListener {
    private val controller = Controller()
    lateinit var transactions: RecyclerView
    var filter: TimeFilter = TimeFilter.CURRENT_DAY
    private var transactions_list = ArrayList<TransactionAdapter.TransactionState>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        transactions = view.findViewById(R.id.transactions)
        transactions.layoutManager = LinearLayoutManager(requireActivity());
        transactions.adapter = TransactionAdapter(transactions_list)

        view.findViewById<FloatingActionButton>(R.id.add_transaction).setOnClickListener(this)
        view.findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            refresh()
            view.findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }

        view.findViewById<Spinner>(R.id.time_choice).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    filter = when (position) {
                        0 -> TimeFilter.CURRENT_DAY
                        1 -> TimeFilter.CURRENT_MONTH
                        2 -> TimeFilter.CURRENT_YEAR
                        else -> TimeFilter.ALL_TIME
                    }
                    refresh()
                }
            }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_options,
            R.layout.spinner_selected_layout
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        view.findViewById<Spinner>(R.id.time_choice).adapter = adapter

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.top_bar)
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.qr_scan -> {
                        startActivityForResult(
                            Intent(requireActivity(), ScannerActivity::class.java),
                            REQUEST_QR
                        )
                        true
                    }
                    R.id.add -> {
                        startActivityForResult(
                            Intent(requireActivity(), AddTransactionActivity::class.java),
                            REQUEST_TRANSACTION
                        )
                        true
                    }
                    else -> false
                }
            }
        return view
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
                    progressbar_db_add.visibility = View.VISIBLE
                    view_under_progressbar.visibility = View.VISIBLE
                    try {
                        data?.getStringExtra("qr")?.let {
                            val scanResult = runBlocking {
                                withContext(Dispatchers.IO) {
                                    controller.scanQR(it)
                                }
                            }
                            controller.addTransaction(scanResult)
                            refresh()
                            progressbar_db_add.visibility = View.INVISIBLE
                            view_under_progressbar.visibility = View.INVISIBLE
                        }
                    } catch (exception: Exception) {
                        progressbar_db_add.visibility = View.INVISIBLE
                        view_under_progressbar.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "QR Scanning Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
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
        transaction.apply { total = items.map { it.total }.sum() }
        controller.addTransaction(transaction)
//        transactions_list.add(TransactionAdapter.TransactionState(data = transaction))
//        transactions.adapter?.notifyDataSetChanged()
        refresh()
    }


    private fun refresh() {
        lifecycleScope.launch {
            transactions_list.clear()
            transactions_list.addAll(controller.getTransactions(filter).map {
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