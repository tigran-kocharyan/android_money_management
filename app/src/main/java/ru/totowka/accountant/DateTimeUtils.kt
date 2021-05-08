package ru.totowka.accountant

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DateTimeUtils {
    private object HOLDER {
        val INSTANCE = DateTimeUtils()
    }

    companion object {
        val instance: DateTimeUtils by lazy { HOLDER.INSTANCE }

        fun toDate(localDateTime: LocalDateTime): Date =
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())

        fun toTimestamp(localDateTime: LocalDateTime) : Timestamp{
            val seconds = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
            val nanos = localDateTime.nano
            return Timestamp(seconds, nanos)
        }
    }
}