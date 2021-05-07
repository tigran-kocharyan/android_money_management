package ru.totowka.accountant.data.type

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(val amount: Int = 0, var price: Double = 0.0, var title: String = "") :
    Parcelable {
    constructor(amount: Int, price: Double, title: String, total: Double) :
            this(amount, price, title) {
        this.total = total
    }

    @Exclude
    var total: Double = 0.0
}