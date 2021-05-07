package ru.totowka.accountant.data

import ru.totowka.accountant.data.type.Transaction

interface TransactionScanner {
    fun getTransactionInfo(qr: String): Transaction

}