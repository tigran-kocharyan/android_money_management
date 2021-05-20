package ru.totowka.accountant.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.data.extension.toDateString
import ru.totowka.accountant.data.type.Product
import ru.totowka.accountant.data.type.Transaction
import kotlin.math.roundToInt

class TransactionAdapter(private val values: ArrayList<TransactionState>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransactionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_transaction, parent, false)
        )

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(values[position])
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var transaction_title: TextView = itemView.findViewById(R.id.transaction_title)
        var transaction_date: TextView = itemView.findViewById(R.id.transaction_date)
        var transaction_total: TextView = itemView.findViewById(R.id.transaction_total)
        val controller = Controller()
        var products: RecyclerView = itemView.findViewById(R.id.products)
        private lateinit var transactionState: TransactionState
        private val adapter = ProductAdapter(ArrayList<Product>())

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

        @SuppressLint("SetTextI18n")
        fun bind(states: TransactionState) {
            transactionState = states
            transaction_title.text = states.data.title
            transaction_date.text = states.data.date.toDateString()
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