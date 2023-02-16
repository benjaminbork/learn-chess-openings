package com.example.learnchessopenings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.learnchessopenings.databinding.OverviewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class OverviewActivity : AppCompatActivity() {
    private lateinit var binding : OverviewBinding
    private lateinit var mainAppBar : androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        mainAppBar = findViewById(R.id.main_app_bar)
        mainAppBar.title = "Home"
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    mainAppBar.title = it.title
                    replaceFragment(Home())
                }
                R.id.courses -> {
                    mainAppBar.title = it.title
                    replaceFragment(Courses())
                }
                R.id.profile -> {
                    mainAppBar.title = it.title
                    replaceFragment(Profile())
                }

                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}