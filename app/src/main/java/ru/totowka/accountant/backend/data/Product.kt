package ru.totowka.accountant.backend.data

data class Product(var amount: Int, var price: Double, var title: String)  {
    constructor() : this(0, 0.0, "") {

    }
}