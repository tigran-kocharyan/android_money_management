package ru.totowka.accountant.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.totowka.accountant.R
import ru.totowka.accountant.data.type.Product

class ProductAdapter(private val values: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_product, parent, false)
        )

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(values[position])
    }

    fun setData(newItems: List<Product>) {
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.product_title)
        private val amount: TextView = view.findViewById(R.id.product_amount)
        private val price: TextView = view.findViewById(R.id.product_price)

        fun bind(data: Product) {
            title.text = data.title
            amount.text = "${data.amount} X ${data.price} ₽"
            price.text = "${data.total} ₽"
        }
    }
}