package ru.totowka.accountant.presentation.secondary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.presentation.primary.MainActivity

class AuthActivity : AppCompatActivity(), View.OnClickListener {
    private val controller = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (controller.isAuthorized()) {
            controller.addUser()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_auth)
        findViewById<Button>(R.id.sign_in).setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        controller.addUser()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else -> Toast.makeText(
                        this,
                        ERROR_SIGN_IN, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.sign_in -> {
                when (controller.isAuthorized()) {
                    true -> startActivity(Intent(this, MainActivity::class.java))
                    false -> startActivityForResult(
                        controller.getAuthIntent(),
                        RC_SIGN_IN
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "WelcomeActivity"
        private const val ERROR_SIGN_IN = "Sing-In in your Google Account at first!";
        private const val RC_SIGN_IN = 9001
    }
}