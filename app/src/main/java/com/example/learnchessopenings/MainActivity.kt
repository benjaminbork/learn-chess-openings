package com.example.learnchessopenings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        var loginBtn = findViewById<Button>(R.id.btn_login)
        loginBtn.setOnClickListener() {
            var overview  = Intent (applicationContext,OverviewActivity::class.java)
            startActivity(overview)
        }
    }
}