package ru.totowka.accountant.data.model

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    fun getUserID(): String? {
        return auth.currentUser?.uid
    }

    fun getAuthIntent(): Intent {
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