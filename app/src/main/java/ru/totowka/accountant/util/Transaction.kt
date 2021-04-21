package ru.totowka.accountant.util

import com.google.firebase.Timestamp

class Transaction(val qrInfo: String, val date: Timestamp, val items: List<Product>) {
}