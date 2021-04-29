package ru.totowka.accountant.backend.data

import com.google.firebase.Timestamp

data class Transaction(var qr_info: String, var date: Timestamp, var items: List<Product>) {
    constructor() : this("", Timestamp.now(), ArrayList<Product>()){

    }
}