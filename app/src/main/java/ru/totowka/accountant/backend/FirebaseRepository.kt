package ru.totowka.accountant.backend

import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.totowka.accountant.data.Transaction
import ru.totowka.accountant.ui.MainActivity

class FirebaseRepository(val auth: FirebaseAuth = FirebaseAuth.getInstance(),
                         val db: FirebaseFirestore = Firebase.firestore) {
    fun addTransaction(transaction: Transaction) {
        val document = hashMapOf<String, Any>(
            "owner" to db.collection("users").document(auth.currentUser!!.uid),
            "qr_info" to transaction.qr_info,
            "transaction_info" to hashMapOf(
                "date" to transaction.date,
                "items" to transaction.items
            )
        )
        db.collection("transactions").document()
            .set(document)
    }

    fun removeTransaction(document_id: String): Boolean {
        return db.collection("user")
            .document(document_id)
            .delete().isSuccessful
    }

    suspend fun getTransactions(): List<DocumentSnapshot>? = withContext(Dispatchers.IO){
        return@withContext try {
            val userRef = db.collection("users").document(auth.currentUser!!.uid)
            val data = db.collection("transactions")
                .whereEqualTo("owner", userRef)
                .get()
                .await()

            Log.d(MainActivity.TAG, "data.documents.size => ${data.documents.size}")
            data.documents
        } catch (e: Exception) {
            null
        }
    }

    fun addUser() {
        val user = hashMapOf<String, Any>(
            "uid" to auth.currentUser!!.uid
        )
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .set(user)
    }

    fun getAuthIntent(): Intent {
        // TODO: вынести providers в локальные переменные или лучше каждый раз создавать?
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    fun isAuthorized(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    companion object {
        private const val TAG = "FirebaseRepository"
    }
}