package ru.totowka.accountant.backend

import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.totowka.accountant.util.Transaction

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun addTransaction(transaction: Transaction) {
        val document = hashMapOf<String, Any>(
            "owner" to db.collection("users").document(auth.currentUser!!.uid),
            "transaction_info" to hashMapOf(
                "date" to transaction.date,
                "items" to transaction.items
            )
        )
        db.collection("transactions").document()
            .set(document)
    }

    fun getTransactions() : List<DocumentSnapshot> {
        val userRef = db.collection("user").document(auth.currentUser!!.uid)
        var documents: List<DocumentSnapshot> = ArrayList()
        db.collection("transactions")
            .whereEqualTo("owner", userRef).get()
            .addOnSuccessListener { result ->
                documents = result.documents
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return documents
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
        private const val TAG = "Firebase"
    }
}