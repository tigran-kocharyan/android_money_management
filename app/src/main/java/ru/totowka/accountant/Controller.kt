package ru.totowka.accountant

import android.content.Intent
import ru.totowka.accountant.backend.data.Transaction
import ru.totowka.accountant.backend.exception.AutorizationException
import ru.totowka.accountant.backend.model.AuthRepository
import ru.totowka.accountant.backend.model.FirestoreRepository
import ru.totowka.accountant.backend.model.TransactionScannerStub

class Controller(
    val fs: FirestoreRepository = FirestoreRepository(),
    val auth: AuthRepository = AuthRepository(),
    val scanner: TransactionScannerStub = TransactionScannerStub()
) {

    fun addUser() {
        auth.getUserID()?.let { fs.addUser(it) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun isAuthorized(): Boolean {
        return auth.isAuthorized()
    }

    suspend fun getTransactions(): List<Transaction> {
        return auth.getUserID()?.let { fs.getTransactions(it) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    fun addTransaction(transaction: Transaction) {
        auth.getUserID()?.let { fs.addTransaction(transaction, it) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    fun scanQR(qr: String): Transaction {
        return scanner.getTransactionInfo(qr)
    }

    companion object {
        const val AUTH_ERROR = "The user isn't authorized"
    }
}