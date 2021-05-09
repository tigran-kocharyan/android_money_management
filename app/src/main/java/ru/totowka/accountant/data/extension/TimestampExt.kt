package ru.totowka.accountant.data.extension

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

fun Timestamp.toLocalDate() = toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun Timestamp.toDateString(): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.CHINESE)
    return formatter.format(toDate()).toString()
}
