package ru.totowka.accountant.data.extension

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDateTime.toDate() =
    Date.from(atZone(ZoneId.systemDefault()).toInstant())

fun LocalDateTime.toTimestamp() =
    Timestamp(atZone(ZoneId.systemDefault()).toEpochSecond(), nano)
