package ru.totowka.accountant.data.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.totowka.accountant.data.TimeFilter
import ru.totowka.accountant.data.type.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.collections.ArrayList

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
                val data = buildQuery(owner, filter).get()
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
                Log.d(TAG, data.size().toString())
                result
            } catch (e: Exception) {
                Log.d(TAG, e.printStackTrace().toString())
                null
            }
        }

    private fun buildQuery(owner: String, filter: TimeFilter): Query {
        val userRef = db.collection("users").document(owner)
        val query = db.collection("transactions")
            .whereEqualTo("owner", userRef)
            .orderBy("transaction_info.date", Query.Direction.DESCENDING)

        val now  = LocalDate.now()
        return when (filter) {
            TimeFilter.CURRENT_DAY -> query.whereGreaterThanOrEqualTo(
                "transaction_info.date", toTimestamp(
                    LocalDateTime.of(now.year, now.month, now.dayOfMonth, 0, 0)
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