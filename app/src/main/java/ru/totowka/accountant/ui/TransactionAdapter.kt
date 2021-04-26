package ru.totowka.accountant.ui

import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import ru.totowka.accountant.R
import ru.totowka.accountant.backend.data.Transaction

class TransactionAdapter(private val values: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_transaction, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.transaction_id?.text = values[position].qr_info
        holder.transaction_date?.text = values[position].date.toDate().toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var transaction_id: TextView? = null
        var transaction_date: TextView? = null
        init {
            transaction_id = itemView.findViewById(R.id.transaction_id)
            transaction_date = itemView.findViewById(R.id.transaction_date)
        }
    }

    companion object {
        const val TAG = "Adapter"
    }
}