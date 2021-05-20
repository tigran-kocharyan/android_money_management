package ru.totowka.accountant.data.utils

import ru.totowka.accountant.data.type.Transaction

interface TransactionScanner {
    suspend fun getTransactionInfo(qr: String): Transaction
}