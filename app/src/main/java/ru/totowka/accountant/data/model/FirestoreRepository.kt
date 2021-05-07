package ru.totowka.accountant.data.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.totowka.accountant.data.type.Transaction

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

    suspend fun getTransactions(owner: String): List<Transaction>? = withContext(Dispatchers.IO) {
        return@withContext try {
            val userRef = db.collection("users").document(owner)
            val data = db.collection("transactions")
                .whereEqualTo("owner", userRef)
                .orderBy("transaction_info.date", Query.Direction.DESCENDING).get()
                .await()

            val result = ArrayList<Transaction>()
            for (document in data.documents) {
                if (document != null) {
                    var transaction = document.getField<Transaction>("transaction_info")!!
                    transaction.apply {
                        document_id = document.id
                        items.map { it.total = it.amount * it.price }
                        total = items.map { it.total }.sum()
                    }
                    result.add(transaction)
                    Log.d(TAG, "Added getField => ${result.size}")
                }
            }
            result
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
            null
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

    companion object {
        private const val TAG = "FirebaseRepository"
    }
}