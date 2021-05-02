package ru.totowka.accountant.backend.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Transaction(var qr_info: String, var date: Timestamp, var items: List<Product>) {
    constructor() : this("", Timestamp.now(), ArrayList<Product>()) {

    }

    @Exclude
    var document_id: String = ""

    @Exclude
    var total: Double = 0.0

    fun setTotal() {
        total = items.map { it.total }.sum()
    }
}