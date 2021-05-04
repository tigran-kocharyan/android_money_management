package ru.totowka.accountant.frontend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.totowka.accountant.R
import ru.totowka.accountant.backend.data.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class TransactionAdapter(private val values: List<TransactionState>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_transaction, parent, false)

        return TransactionViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(values[position])
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var transaction_title: TextView = itemView.findViewById(R.id.transaction_title)
        var transaction_date: TextView = itemView.findViewById(R.id.transaction_date)
        var transaction_total: TextView = itemView.findViewById(R.id.transaction_total)
        var products: RecyclerView = itemView.findViewById(R.id.products)
        private lateinit var transactionState: TransactionState
        private val adapter = ProductAdapter()

        init {
            val parent = itemView.findViewById<ViewGroup>(R.id.parent_layout)
            parent.layoutTransition.setAnimateParentHierarchy(false)
            products.adapter = adapter
            parent.setOnClickListener {
                if (::transactionState.isInitialized) {
                    transactionState.isExpand = !transactionState.isExpand
                    showDetails(transactionState.isExpand)
                }
            }
        }

        private fun showDetails(expand: Boolean) {
            if (expand) {
                products.visibility = View.VISIBLE
            } else {
                products.visibility = View.GONE
            }
        }

        fun bind(states: TransactionState) {
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.CHINESE)
            transactionState = states
            transaction_title.text = states.data.document_id
            transaction_date.text = formatter.format(states.data.date.toDate()).toString()
            transaction_total.text = "${states.data.total.roundToInt()} ₽"

            adapter.setData(states.data.items)
            showDetails(states.isExpand)
        }
    }

    data class TransactionState(
        var isExpand: Boolean = false,
        val data: Transaction
    )

    companion object {
        const val TAG = "Adapter"
    }
}