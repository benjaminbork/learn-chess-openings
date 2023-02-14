package com.example.learnchessopenings

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class DetailedCourse() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_detail_card)

        val data = intent.getIntExtra("id", 0)
        Log.d("eee", data.toString())
    }

}