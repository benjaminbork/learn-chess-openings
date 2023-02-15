package com.example.learnchessopenings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class DetailedCourse() : AppCompatActivity() {
    private var db: DbHelper = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_detail_card)

        db = DbHelper(this)

        val data = intent.getIntExtra("id", 0)

        populatePage(data)
    }

    private fun populatePage(courseId: Int) {

        val data = getData(courseId)

        val mainAppBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_app_bar)
        mainAppBar.title = ""
    }

    private fun getData(courseId: Int) {

    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

}