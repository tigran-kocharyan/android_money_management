package ru.totowka.accountant.data.extension

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(this.seconds),
        TimeZone.getDefault().toZoneId()
    )
}

fun Timestamp.toDateString(): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.CHINESE)
    return formatter.format(toDate()).toString()
}

fun Timestamp.toLocalDate() =
    toLocalDateTime().toLocalDate()
