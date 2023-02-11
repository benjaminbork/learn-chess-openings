package com.example.learnchessopenings

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.user
import com.example.learnchessopenings.Models.variation
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private val db = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val readDb = db.readableDatabase
        val cursor = readDb.query(
            user.User.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        checkStoredCourses()

        // Checks if a user already exists and reroutes to the homepage if it's true
        if(cursor.count < 1) {
            cursor.close()
            setContentView(R.layout.login_screen)
        }
        else {
            cursor.close()
            val overview = Intent (applicationContext, OverviewActivity::class.java)
            overview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(overview)
            finish()
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    fun createUser(view: View) {
        val writeDb = db.writableDatabase
        val textInput = findViewById<View>(R.id.textInputLayout_username) as TextInputLayout
        val name = textInput.editText?.text
        if(name != null) {
            val values = ContentValues().apply {
                put(user.User.COLUMN_NAME_NAME, name.toString())
                put(user.User.COLUMN_NAME_STREAK, 0)
                put(user.User.COLUMN_NAME_XP, 0)
            }
            val newRowId = writeDb.insert(user.User.TABLE_NAME, null, values)

            if(newRowId != (-1).toLong()) {
                val overview = Intent (applicationContext, OverviewActivity::class.java)
                overview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(overview)
                finish()
            }
        }
    }

    private fun checkStoredCourses() {
        val readDb = db.readableDatabase

        for(c in coursesData) {
            val cursor = readDb.query(
                course.Course.TABLE_NAME,
                null,
                "${course.Course.COLUMN_NAME_TITLE} = ?",
                arrayOf(c[course.Course.COLUMN_NAME_TITLE].toString()),
                null,
                null,
                null
            )
            if(cursor.count < 1) {
                // Course doesn't exist
                storeCourse(c)
            }
            cursor.close()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun storeCourse(c: Map<String, Any>) {
        val writeDb = db.writableDatabase

        var variationIds: Array<Int> = arrayOf()
        for(v in c[course.Course.COLUMN_NAME_VARIATIONS] as Array<Map<String, Any>>) {
            variationIds += storeVariation(v, writeDb)
        }

        val values = ContentValues().apply {
            put(course.Course.COLUMN_NAME_TITLE, c[course.Course.COLUMN_NAME_TITLE].toString())
            put(course.Course.COLUMN_NAME_ACTIVE, 0)
            put(course.Course.COLUMN_NAME_BLACK, c[course.Course.COLUMN_NAME_BLACK].toString())
            put(course.Course.COLUMN_NAME_VARIATIONS, variationIds.map{ it.toString() }.reduce {x, y -> "$x, $y"})
        }

        writeDb.insert(
            course.Course.TABLE_NAME,
            null,
            values
        )
    }

    private fun storeVariation(v: Map<String, Any>, writeDb: SQLiteDatabase): Int {
        return 17
    }

    private val coursesData: Array<Map<String, Any>> = arrayOf(
        mapOf(
            course.Course.COLUMN_NAME_TITLE to "Example course",
            course.Course.COLUMN_NAME_BLACK to 0,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "First Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "I don't know how FEN's work pls help",
                        "Seriously, idk"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Second Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "I still don't know how FEN's work pls help",
                        "Seriously, idk"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN"
                    )
                )
            )
        ),
        mapOf(
            course.Course.COLUMN_NAME_TITLE to "Other example course",
            course.Course.COLUMN_NAME_BLACK to 1,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "First Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "I don't know how FEN's work pls help",
                        "Seriously, idk"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN"
                    )
                )
            )
        )
    )
}