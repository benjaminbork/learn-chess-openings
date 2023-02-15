package com.example.learnchessopenings

import android.os.Bundle
import android.provider.BaseColumns
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.variation


class DetailedCourse : AppCompatActivity() {
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
        mainAppBar.title = data[course.Course.COLUMN_NAME_TITLE].toString()

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