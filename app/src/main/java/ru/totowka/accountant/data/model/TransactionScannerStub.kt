package ru.totowka.accountant.data.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.firebase.Timestamp
import edu.mipt.melges.sliploader.SlipLoaderClient
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import ru.totowka.accountant.data.TransactionScanner
import ru.totowka.accountant.data.extension.toTimestamp
import ru.totowka.accountant.data.type.Product
import ru.totowka.accountant.data.type.Transaction
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TransactionScannerStub : TransactionScanner {
    override suspend fun getTransactionInfo(qr: String): Transaction {
        return Transaction(
            qr, LocalDateTime.parse(
                qr.substring(2, 15),
                DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")
            ).toTimestamp(),
            // TODO: Получить настоящий список продуктов
            listOf(
                Product(1, 1.0, "product_1"),
                Product(2, 2.0, "product_2")
            )
        )
    }

    companion object {
        const val TAG = "TransactionScannerStub"
    }
}