package ru.totowka.accountant.data.type

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(val title: String, val date: Timestamp, val items: List<Product>) :
    Parcelable {
    constructor() : this("Product", Timestamp.now(), ArrayList<Product>())

    @Exclude
    var document_id: String = ""

    @Exclude
    var total: Double = 0.0
}