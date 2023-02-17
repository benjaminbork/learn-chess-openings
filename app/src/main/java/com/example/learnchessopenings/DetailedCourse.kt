package com.example.learnchessopenings

import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.Adapters.detailedCourseAdapter
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.variation
import com.example.learnchessopenings.ViewModels.detailedCourseViewModel


class DetailedCourse : AppCompatActivity(), detailedCourseAdapter.OnItemClickListener {
    private var db: DbHelper = DbHelper(this)
    private lateinit var courseTitle : String
    private var courseId = 0
    private var coursePlayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_detail_card)

        db = DbHelper(this)

        courseId = intent.getIntExtra("id", 0)
        populatePage(courseId)
    }

    private fun populatePage(courseId: Int) {
        val data = getData(courseId)
        courseTitle = data[course.Course.COLUMN_NAME_TITLE].toString()
        coursePlayer = data[course.Course.COLUMN_NAME_BLACK] as Int

        val returnAppBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.returnAppBar)

        returnAppBar.title = courseTitle
        returnAppBar.setNavigationOnClickListener {
            finish()
        }


        var progress = 0
        val variations = data[course.Course.COLUMN_NAME_VARIATIONS] as ArrayList<Map<String, *>>
        for(variation in variations) {
            if(variation["learned"] == 1) {
                progress += 1
            }
        }
        val courseProgressText = findViewById<TextView>(R.id.variationProgressCount)
        val courseProgressBar = findViewById<ProgressBar>(R.id.variationProgressBar)
        courseProgressText.text = "${progress}/${variations.size}"
        courseProgressBar.max = variations.size
        courseProgressBar.progress = progress

        val courseDetails = findViewById<TextView>(R.id.courseDetailDescription)
        courseDetails.text = data[course.Course.COLUMN_NAME_DESCRIPTION].toString()

        val courseImg = findViewById<ImageView>(R.id.courseDetailImage)
        courseImg.setImageResource(data[course.Course.COLUMN_NAME_IMAGE_ID] as Int)

        val reviewBtn = findViewById<Button>(R.id.reviewButton)
        reviewBtn.text = "Review All (${progress})"
        reviewBtn.setOnClickListener {
            // Review all button code

        }

        populateRecycler(findViewById(R.id.recyclerView), variations)
    }

    private fun populateRecycler(recyclerView: RecyclerView, variations: ArrayList<Map<String, *>>) {
        val data = ArrayList<detailedCourseViewModel>()
        for(vari in variations) {
            data.add(detailedCourseViewModel(
                vari[BaseColumns._ID] as Int,
                vari[variation.Variation.COLUMN_NAME_TITLE].toString(),
                if(vari[variation.Variation.COLUMN_NAME_LEARNED] as Int != 1) false else true
            ))
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = detailedCourseAdapter(data, this)
    }

    override fun onItemClick(id: Int) {
        val intent = Intent(applicationContext, LearnActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("courseTitle", courseTitle)
        intent.putExtra("coursePlayer", coursePlayer)
        intent.putExtra("courseId", courseId)
        course.setActive(db, id)
        startActivity(intent)
        finish()
    }

    private fun getData(courseId: Int): Map<String, Any> {
        val readDb = db.readableDatabase
        var data = mapOf<String, Any>()

        val cursor = readDb.query(
            course.Course.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(courseId.toString()),
            null,
            null,
            null
        )
        while(cursor.moveToNext()) {
            data = mapOf(
                BaseColumns._ID to cursor.getInt(0),
                course.Course.COLUMN_NAME_TITLE to cursor.getString(1),
                course.Course.COLUMN_NAME_ACTIVE to cursor.getInt(2),
                course.Course.COLUMN_NAME_BLACK to cursor.getInt(3),
                course.Course.COLUMN_NAME_DESCRIPTION to cursor.getString(4),
                course.Course.COLUMN_NAME_IMAGE_ID to cursor.getInt(5),
                course.Course.COLUMN_NAME_VARIATIONS to variation.getVariations(cursor.getString(6), db)
            )
        }
        cursor.close()

        return data
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

}