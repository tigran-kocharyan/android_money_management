package ru.totowka.accountant.backend.data

class Product(val amount: Int, val price: Double, val title: String)  {
    fun toMap() : HashMap<String, Any>{
        return hashMapOf(
            "amount" to amount,
            "summary" to price,
            "title" to title
        )
    }
}