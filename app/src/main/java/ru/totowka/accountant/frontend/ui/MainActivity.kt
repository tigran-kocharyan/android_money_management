package ru.totowka.accountant.frontend.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.totowka.accountant.R


class MainActivity : AppCompatActivity() {

    val analysisFragment: Fragment = AnalysisFragment()
    val transactionsFragment: Fragment = TransactionsFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = transactionsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.fragment_container, analysisFragment, "2")
                .hide(analysisFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, transactionsFragment, "1")
                .commit();
        }

        BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.getItemId()) {
                R.id.transaction_list -> {
                    fm.beginTransaction().hide(active).show(transactionsFragment).commit()
                    active = transactionsFragment
                    true
                }
                R.id.transaction_analysis -> {
                    fm.beginTransaction().hide(active).show(analysisFragment).commit()
                    active = analysisFragment
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_QR = 1661
    }
}