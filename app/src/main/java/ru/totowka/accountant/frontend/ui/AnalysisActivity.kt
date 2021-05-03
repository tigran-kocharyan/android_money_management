package ru.totowka.accountant.frontend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.totowka.accountant.R

class AnalysisActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.transaction_list -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


    companion object {
        const val TAG = "AnalysisActivity"
    }
}