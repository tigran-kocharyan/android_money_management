package ru.totowka.accountant.data

import com.google.firebase.Timestamp

class Transaction(val qr_info: String,
                  val date: Timestamp,
                  val items: List<Product>) {
}