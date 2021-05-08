package ru.totowka.accountant.data.type

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val amount: Int = 0,
    var price: Double = 0.0,
    var title: String = "",
    var total: Double = 0.0
) :
    Parcelable {
}