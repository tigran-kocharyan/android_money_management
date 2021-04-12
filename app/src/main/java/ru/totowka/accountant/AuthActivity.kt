package ru.totowka.accountant

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var mSignin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        mSignin = findViewById(R.id.sign_in)
        mSignin.setOnClickListener(View.OnClickListener {
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
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                val intent: Intent = Intent(
                    this,
                    MainActivity::class.java
                ).apply {
                    putExtra("user_name", user)
                }
                startActivity(intent)
            } else {
                val toast = Toast.makeText(this, ERROR_SIGN_IN, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    companion object {
        private const val TAG = "WelcomeActivity"
        private const val ERROR_SIGN_IN = "Sing-In in your Google Account at first!";
        private const val RC_SIGN_IN = 9001
    }
}