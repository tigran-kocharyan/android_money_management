package ru.totowka.accountant.data

class Product(val amount: Int, val price: Double, val title: String)  {
    fun toHashMap() : HashMap<String, Any>{
        return hashMapOf<String, Any>(
            "amount" to amount,
            "summary" to price,
            "title" to title
        )
    }
}