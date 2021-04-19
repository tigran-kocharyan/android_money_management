package ru.totowka.accountant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var mSignin: Button
    private lateinit var mTitle: TextView
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        mTitle = findViewById<TextView>(R.id.title)
        mSignin = findViewById(R.id.sign_in)
        mSignin.setOnClickListener(View.OnClickListener {
            when (FirebaseAuth.getInstance().currentUser) {
                null -> {
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                        RC_SIGN_IN
                    )
                }
                else -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = hashMapOf<String, Any>(
                    "uid" to mAuth.currentUser!!.uid
                )
                db.collection("users")
                    .document(mAuth.currentUser!!.uid)
                    .set(user)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, ERROR_SIGN_IN, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "WelcomeActivity"
        private const val ERROR_SIGN_IN = "Sing-In in your Google Account at first!";
        private const val RC_SIGN_IN = 9001
    }
}