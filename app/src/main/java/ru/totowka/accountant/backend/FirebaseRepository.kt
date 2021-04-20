package ru.totowka.accountant.backend

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun addUserToFirestore() {
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
        private const val RC_SIGN_IN = 9001
    }
}