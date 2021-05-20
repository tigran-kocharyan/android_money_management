package ru.totowka.accountant.presentation.primary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.totowka.accountant.R

class MainActivity : AppCompatActivity() {

    private val analysisFragment: Fragment =
        AnalysisFragment()
    private val transactionsFragment: Fragment =
        ListFragment()
    private var activeFragment: Fragment = transactionsFragment
    private val fm: FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.fragment_container, analysisFragment)
                .hide(analysisFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, transactionsFragment)
                .commit();
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener {
                when (it.getItemId()) {
                    R.id.transaction_list -> {
                        fm.beginTransaction().hide(activeFragment).show(transactionsFragment)
                            .commit()
                        activeFragment = transactionsFragment
                        true
                    }
                    R.id.transaction_analysis -> {
                        fm.beginTransaction().hide(activeFragment).show(analysisFragment).commit()
                        activeFragment = analysisFragment
                        true
                    }
                    else -> false
                }
            }

    }

    companion object {
        const val TAG = "MainActivity"
    }
}