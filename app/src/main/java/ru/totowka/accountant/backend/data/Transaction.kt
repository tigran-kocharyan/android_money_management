package ru.totowka.accountant.backend.data

import com.google.firebase.Timestamp

data class Transaction(val qr_info: String,
                  val date: Timestamp,
                  val items: List<Product>) {

    fun toMap() : HashMap<String, Any> {
        return hashMapOf(
                "qr_info" to qr_info,
                "date" to date,
                "items" to items
            )
    }
}