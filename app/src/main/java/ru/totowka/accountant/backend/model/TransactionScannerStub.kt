package ru.totowka.accountant.backend.model

import android.util.Log
import com.google.firebase.Timestamp
import ru.totowka.accountant.backend.TransactionScanner
import ru.totowka.accountant.backend.data.Product
import ru.totowka.accountant.backend.data.Transaction
import java.util.*

class TransactionScannerStub : TransactionScanner {
    override fun getTransactionInfo(qr: String): Transaction {
        return Transaction(
            qr, qr.substring(2, 15).toTimestamp(),
            // TODO: Получить настоящий список продуктов
            listOf(
                Product(1, 1.0, "product_1"),
                Product(2, 2.0, "product_2")
            )
        )
    }

    private fun String.toTimestamp(): Timestamp {
        Log.d(TAG, this)
        return Timestamp(
            Date(
                // TODO: пофиксить костыли с датами.
                this.substring(0, 4).toInt() - 1900, this.substring(4, 6).toInt() - 1,
                this.substring(6, 8).toInt(), this.substring(9, 11).toInt(),
                this.substring(11, 13).toInt()
            )
        )
    }

    companion object {
        const val TAG = "TransactionScannerStub"
    }
}