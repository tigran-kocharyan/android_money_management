package edu.mipt.melges.sliploader

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import ru.totowka.accountant.data.TransactionScanner
import ru.totowka.accountant.data.extension.toTimestamp
import ru.totowka.accountant.data.type.Product
import ru.totowka.accountant.data.type.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class RemoteTransactionScanner : TransactionScanner {
    companion object {
        val qrDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm[ss]")
    }

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                registerModule(JavaTimeModule())
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }

    private val slipLoader = SlipLoaderClient(client, "2224.sMMTJTqgbpkWZihad")

    override suspend fun getTransactionInfo(qr: String): Transaction {
        val resolvedSlipInfo = slipLoader.resolveSlipInfo(parseQrString(qr))
        return Transaction(
            resolvedSlipInfo.data.json.retailPlace,
            resolvedSlipInfo.data.json.dateTime.toTimestamp(),
            resolvedSlipInfo.data.json.items.map {
                Product(
                    it.quantity.toInt(),
                    it.price.toDouble() / 100,
                    it.name, it.sum.toDouble() / 100
                )
            }
        )
    }

    private fun parseQrString(qrString: String): SlipDocumentData {
        val qrArgs = qrString.split("&")
            .map { it.split("=") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .toMap()
        return SlipDocumentData(
            qrArgs["fn"]!!.toLong(),
            qrArgs["i"]!!.toLong(),
            qrArgs["fp"]!!.toLong(),
            LocalDateTime.parse(qrArgs["t"]!!, qrDateTimeFormatter),
            qrArgs["n"]!!.toInt(),
            BigDecimal(qrArgs["s"]).movePointRight(2).toLong()
        )
    }
}