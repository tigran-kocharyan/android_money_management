package ru.totowka.accountant.backend

import ru.totowka.accountant.backend.data.Transaction

interface TransactionScanner {
    fun getTransactionInfo(qr: String): Transaction

}