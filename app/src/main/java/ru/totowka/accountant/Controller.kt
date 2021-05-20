package ru.totowka.accountant

import android.content.Intent
import ru.totowka.accountant.data.utils.AutorizationException
import ru.totowka.accountant.data.utils.TimeFilter
import ru.totowka.accountant.data.utils.TransactionScanner
import ru.totowka.accountant.data.extension.toLocalDate
import ru.totowka.accountant.data.extension.toLocalDateTime
import ru.totowka.accountant.data.model.AuthRepository
import ru.totowka.accountant.data.model.FirestoreRepository
import ru.totowka.accountant.data.model.RemoteTransactionScanner
import ru.totowka.accountant.data.type.ChartTransactionState
import ru.totowka.accountant.data.type.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class Controller(
    private val fs: FirestoreRepository = FirestoreRepository(),
    private val auth: AuthRepository = AuthRepository(),
    private val scanner: TransactionScanner = RemoteTransactionScanner()
) {


    fun addUser() {
        auth.getUserID()?.let { fs.addUser(it) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun isAuthorized(): Boolean = auth.isAuthorized()

    suspend fun getTransactions(filter: TimeFilter): List<Transaction> {
        return auth.getUserID()?.let { fs.getTransactions(it, filter) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    suspend fun getChartTransactionStatesByFilter(filter: TimeFilter): List<ChartTransactionState> {
        val transactions = auth.getUserID()?.let { fs.getTransactions(it, filter) }
            ?: throw AutorizationException(AUTH_ERROR)

        return transactions.groupBy { it.date.toLocalDateTime() }.values.map { transaction ->
            ChartTransactionState(
                transaction.map { it.total }.sum(),
                transaction[0].date.toLocalDate()
            )
        }
    }

    suspend fun getChartTransactionStatesByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ChartTransactionState> {
        val transactions = auth.getUserID()
            ?.let { fs.getTransactionsByDates(it, startDate, endDate) }
            ?: throw AutorizationException(AUTH_ERROR)

        return transactions.groupBy { it.date.toLocalDate() }.values.map { transaction ->
            ChartTransactionState(
                transaction.map { it.total }.sum(),
                transaction[0].date.toLocalDate()
            )
        }
    }

    fun addTransaction(transaction: Transaction) {
        auth.getUserID()?.let { fs.addTransaction(transaction, it) }
            ?: throw AutorizationException(AUTH_ERROR)
    }

    fun createTotalState(
        values: List<ChartTransactionState>,
        startDate: LocalDate,
        endDate: LocalDate
    ): ArrayList<Double> {
        val startMonth = ArrayList<ChartTransactionState>().apply {
            for (i in 1..YearMonth.of(startDate.year, startDate.month).lengthOfMonth()) {
                add(
                    ChartTransactionState(
                        0.0,
                        LocalDate.of(startDate.year, startDate.month, i)
                    )
                )
            }
            while (size < 31) {
                add(ChartTransactionState(0.0, LocalDate.of(startDate.year, 8, size)))
            }
        }
        val endMonth = ArrayList<ChartTransactionState>().apply {
            for (i in 1..YearMonth.of(endDate.year, endDate.month).lengthOfMonth()) {
                add(ChartTransactionState(0.0, LocalDate.of(endDate.year, endDate.month, i)))
            }
            while (size < 31) {
                add(ChartTransactionState(0.0, LocalDate.of(endDate.year, 8, size)))
            }
        }
        for (value in values) {
            if (value.date.month == startDate.month) {
                startMonth[value.date.dayOfMonth - 1] = value
            } else {
                endMonth[value.date.dayOfMonth - 1] = value
            }
        }
        return ArrayList<Double>().apply {
            for (i in 0..30) {
                add(endMonth[i].total - startMonth[i].total)
            }
        }
    }

    suspend fun scanQR(qr: String): Transaction {
        return scanner.getTransactionInfo(qr)
    }

    companion object {
        const val AUTH_ERROR = "The user isn't authorized"


    }
}