package ru.totowka.accountant.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.totowka.accountant.data.utils.TimeFilter
import ru.totowka.accountant.data.extension.toTimestamp
import ru.totowka.accountant.data.type.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

class FirestoreRepository(val db: FirebaseFirestore = Firebase.firestore) {
    fun addTransaction(transaction: Transaction, owner: String) {
        val document = hashMapOf<String, Any>(
            "owner" to db.collection("users").document(owner),
            "transaction_info" to transaction
        )
        val reference = db.collection("transactions").document()
        transaction.document_id = reference.id
        reference.set(document)
    }

    fun removeTransaction(document_id: String): Boolean {
        return db.collection("user")
            .document(document_id)
            .delete().isSuccessful
    }

    suspend fun getTransactions(owner: String, filter: TimeFilter): List<Transaction>? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val data = buildQueryByFilter(owner, filter).get()
                    .await()

                val result = ArrayList<Transaction>()
                for (document in data.documents) {
                    if (document != null) {
                        val transaction = document.getField<Transaction>("transaction_info")!!
                        transaction.apply {
                            document_id = document.id
                            items.map { it.total = it.amount * it.price }
                            total = items.map { it.total }.sum()
                        }
                        result.add(transaction)
                    }
                }
                result
            } catch (e: Exception) {
                null
            }
        }

    suspend fun getTransactionsByDates(
        owner: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                var startEnd =
                    YearMonth.of(startDate.year, startDate.month).atEndOfMonth().atTime(23, 59)
                var endStart =
                    LocalDateTime.of(endDate.year, endDate.month, 1, 0, 0)
                val start = buildQueryByMonth(
                    owner,
                    startDate.toTimestamp(),
                    startEnd.toTimestamp()
                ).get()
                    .await()
                val end = buildQueryByMonth(
                    owner,
                    endStart.toTimestamp(),
                    endDate.toTimestamp()
                ).get()
                    .await()
                val result = ArrayList<Transaction>()
                for (document in start.documents) {
                    if (document != null) {
                        val transaction = document.getField<Transaction>("transaction_info")!!
                        transaction.apply {
                            document_id = document.id
                            items.map { it.total = it.amount * it.price }
                            total = items.map { it.total }.sum()
                        }
                        result.add(transaction)
                    }
                }
                for (document in end.documents) {
                    if (document != null) {
                        val transaction = document.getField<Transaction>("transaction_info")!!
                        transaction.apply {
                            document_id = document.id
                            items.map { it.total = it.amount * it.price }
                            total = items.map { it.total }.sum()
                        }
                        result.add(transaction)
                    }
                }
                result
            } catch (e: Exception) {
                null
            }
        }

    private fun buildQueryByMonth(owner: String, start: Timestamp, end: Timestamp): Query {
        val userRef = db.collection("users").document(owner)
        return db.collection("transactions")
            .whereEqualTo("owner", userRef)
            .orderBy("transaction_info.date", Query.Direction.DESCENDING)
            .whereLessThanOrEqualTo("transaction_info.date", end)
            .whereGreaterThanOrEqualTo("transaction_info.date", start)
    }

    private fun buildQueryByFilter(owner: String, filter: TimeFilter): Query {
        val userRef = db.collection("users").document(owner)
        val query = db.collection("transactions")
            .whereEqualTo("owner", userRef)
            .orderBy("transaction_info.date", Query.Direction.DESCENDING)

        val now = LocalDate.now()
        return when (filter) {
            TimeFilter.CURRENT_DAY -> query.whereGreaterThanOrEqualTo(
                "transaction_info.date", toTimestamp(
                    LocalDateTime.of(now.year, now.month, now.dayOfMonth, 0, 0)
                )
            ).whereLessThanOrEqualTo(
                "transaction_info.date", toTimestamp(
                    LocalDateTime.of(now.year, now.month, now.dayOfMonth, 23, 59)
                )
            )
            TimeFilter.CURRENT_MONTH -> query.whereGreaterThanOrEqualTo(
                "transaction_info.date", toTimestamp(
                    LocalDateTime.of(now.year, now.month, 1, 0, 0)
                )
            )
            TimeFilter.CURRENT_YEAR -> query.whereGreaterThanOrEqualTo(
                "transaction_info.date", toTimestamp(
                    LocalDateTime.of(now.year, 1, 1, 0, 0)
                )
            )
            else -> query
        }
    }

    fun addUser(uid: String) {
        db.collection("users")
            .document(uid)
            .set(
                hashMapOf<String, Any>(
                    "uid" to uid
                )
            )
    }

    private fun toTimestamp(localDateTime: LocalDateTime): Timestamp {
        val seconds = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
        val nanos = localDateTime.nano
        return Timestamp(seconds, nanos)
    }

    companion object {
        private const val TAG = "FirebaseRepository"
    }
}