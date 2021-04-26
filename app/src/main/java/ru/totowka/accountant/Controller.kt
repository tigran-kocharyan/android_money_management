package ru.totowka.accountant

import android.content.Intent
import com.google.firebase.firestore.DocumentSnapshot
import ru.totowka.accountant.backend.model.AuthRepository
import ru.totowka.accountant.backend.model.FirestoreRepository
import ru.totowka.accountant.backend.data.Transaction
import ru.totowka.accountant.backend.exception.AutorizationException
import ru.totowka.accountant.backend.model.TransactionScannerStub

class Controller(
    val fs: FirestoreRepository = FirestoreRepository(),
    val auth: AuthRepository = AuthRepository(),
    val scanner: TransactionScannerStub = TransactionScannerStub()) {

    fun addUser() {
        when (auth.getUserID()) {
            null -> throw AutorizationException("User should be authorized!")
            else -> fs.addUser(auth.getUserID()!!)
        }
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun isAuthorized(): Boolean {
        return auth.isAuthorized()
    }

    suspend fun getTransactions(): List<DocumentSnapshot>? {
        return auth.getUserID()?.let { fs.getTransactions(it) }
    }

    fun addTransaction(transaction: Transaction) {
        auth.getUserID()?.let { fs.addTransaction(transaction, it) }
    }

    fun scanQR(qr: String) : Transaction{
        return scanner.getTransactionInfo(qr)
    }
}