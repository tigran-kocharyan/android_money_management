package ru.totowka.accountant.backend.data

import com.google.firebase.firestore.Exclude

data class Product(var amount: Int, var price: Double, var title: String) {
    constructor() : this(0, 0.0, "") {}

    @Exclude
    var total: Double = 0.0

    fun calculateTotal() {
        total = amount * price
    }
}