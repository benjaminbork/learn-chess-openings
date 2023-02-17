package com.example.learnchessopenings

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.dailyPuzzle
import com.example.learnchessopenings.Models.user
import com.example.learnchessopenings.Models.variation
import com.google.android.material.textfield.TextInputLayout
import java.util.*

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
        val cursorCount = cursor.count

        checkStoredCourses()
        checkDailyPuzzle()

        // Checks if a user already exists and reroutes to the homepage if it's true
        if(cursorCount < 1) {
            cursor.close()
            setContentView(R.layout.login_screen)
        }
        else {
            cursor.close()
            user.checkWeek(db)
            user.checkStreak(db)
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

    private fun checkDailyPuzzle() {
        val readDb = db.readableDatabase

        val cursor = readDb.query(
            dailyPuzzle.DailyPuzzle.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        if(cursor.count < 1) {
            // Daily puzzle doesn't exist
            storeDailyPuzzle()
        }
        cursor.close()
    }

    private fun storeDailyPuzzle() {
        val writeDb = db.writableDatabase

        val value = ContentValues().apply {
            put(dailyPuzzle.DailyPuzzle.COLUMN_NAME_LAST_PLAYED, "1970-01-01")
        }

        writeDb.insert(
            dailyPuzzle.DailyPuzzle.TABLE_NAME,
            null,
            value
        )
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
                put(user.User.COLUMN_NAME_LAST_DAY_STREAK, "1970-01-01")
                put(user.User.COLUMN_NAME_WEEK, Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.WEEK_OF_YEAR))
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

        var variationIds: Array<Long> = arrayOf()
        for(v in c[course.Course.COLUMN_NAME_VARIATIONS] as Array<Map<String, Any>>) {
            variationIds += storeVariation(v, writeDb)
        }

        val values = ContentValues().apply {
            put(course.Course.COLUMN_NAME_TITLE, c[course.Course.COLUMN_NAME_TITLE].toString())
            put(course.Course.COLUMN_NAME_ACTIVE, 0)
            put(course.Course.COLUMN_NAME_BLACK, c[course.Course.COLUMN_NAME_BLACK].toString())
            put(course.Course.COLUMN_NAME_DESCRIPTION, c[course.Course.COLUMN_NAME_DESCRIPTION].toString())
            put(course.Course.COLUMN_NAME_IMAGE_ID, c[course.Course.COLUMN_NAME_IMAGE_ID] as Int)
            put(course.Course.COLUMN_NAME_VARIATIONS, variationIds.map{ it.toString() }.reduce {x, y -> "$x, $y"})
        }

        writeDb.insert(
            course.Course.TABLE_NAME,
            null,
            values
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun storeVariation(v: Map<String, Any>, writeDb: SQLiteDatabase): Long {

        val fenList: Array<String> = v[variation.Variation.COLUMN_NAME_FEN] as Array<String>
        val commentList: Array<String> = v[variation.Variation.COLUMN_NAME_COMMENTS] as Array<String>

        val values = ContentValues().apply {
            put(variation.Variation.COLUMN_NAME_TITLE, v[variation.Variation.COLUMN_NAME_TITLE].toString())
            put(variation.Variation.COLUMN_NAME_STREAK, 0)
            put(variation.Variation.COLUMN_NAME_LEARNED, 0)
            put(variation.Variation.COLUMN_NAME_FEN, fenList.joinToString(separator = ",-,"))
            put(variation.Variation.COLUMN_NAME_COMMENTS, commentList.joinToString(separator = ",-,"))
        }

        return writeDb.insert(
            variation.Variation.TABLE_NAME,
            null,
            values
        )
    }

    private val coursesData: Array<Map<String, Any>> = arrayOf(
        mapOf(
            course.Course.COLUMN_NAME_TITLE to "Example course",
            course.Course.COLUMN_NAME_BLACK to 0,
            course.Course.COLUMN_NAME_DESCRIPTION to "This is a description!",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable.bk,
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
            course.Course.COLUMN_NAME_DESCRIPTION to "This is another description!",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable.bb,
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
        ),
        mapOf(
            course.Course.COLUMN_NAME_TITLE to "ANOTHER Example course",
            course.Course.COLUMN_NAME_BLACK to 0,
            course.Course.COLUMN_NAME_DESCRIPTION to "This is a description!",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable.bq,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Fiirst Variation",
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
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Third Variation",
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
    )
}