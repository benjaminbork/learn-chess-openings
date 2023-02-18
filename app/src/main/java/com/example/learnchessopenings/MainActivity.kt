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
        readDb.close()
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
        writeDb.close()
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
            course.Course.COLUMN_NAME_TITLE to "French",
            course.Course.COLUMN_NAME_BLACK to 1,
            course.Course.COLUMN_NAME_DESCRIPTION to "Solid opening for black",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable._7099,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Variation #01",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Preparing d5
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Fighting for the center
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        // Creating pawn breaktrough
                        "rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/pp3ppp/4p3/2ppP3/3P4/8/PPP2PPP/RNBQKBNR",
                        // Preparing Nec6
                        "rnbqkbnr/pp3ppp/4p3/2ppP3/3P4/2P5/PP3PPP/RNBQKBNR",
                        "rnbqkb1r/pp2nppp/4p3/2ppP3/3P4/2P5/PP3PPP/RNBQKBNR",
                        // Kingside knight found his dream square
                        "rnbqkb1r/pp2nppp/4p3/2ppP3/3P4/2P2N2/PP3PPP/RNBQKB1R",
                        "rnbqkb1r/pp3ppp/2n1p3/2ppP3/3P4/2P2N2/PP3PPP/RNBQKB1R",
                        // Stopping b4 for good
                        "rnbqkb1r/pp3ppp/2n1p3/2ppP3/3P4/P1P2N2/1P3PPP/RNBQKB1R",
                        "rnbqkb1r/1p3ppp/2n1p3/p1ppP3/3P4/P1P2N2/1P3PPP/RNBQKB1R",
                        // Connecting the pawns
                        "rnbqkb1r/1p3ppp/2n1p3/p1ppP3/P2P4/2P2N2/1P3PPP/RNBQKB1R",
                        "rnbqkb1r/5ppp/1pn1p3/p1ppP3/P2P4/2P2N2/1P3PPP/RNBQKB1R",
                        //Try to create outpost on b4
                        "rnbqkb1r/5ppp/1pn1p3/p1ppP3/P2P4/N1P2N2/1P3PPP/R1BQKB1R",
                        "rnbqkb1r/5ppp/1pn1p3/p2pP3/P2p4/N1P2N2/1P3PPP/R1BQKB1R",
                        // Developing a piece
                        "rnbqkb1r/5ppp/1pn1p3/p2pP3/P2P4/N4N2/1P3PPP/R1BQKB1R",
                        "rnbqk2r/4bppp/1pn1p3/p2pP3/P2P4/N4N2/1P3PPP/R1BQKB1R",
                        // Getting the king safe
                        "rnbqk2r/4bppp/1pn1p3/pN1pP3/P2P4/5N2/1P3PPP/R1BQKB1R",
                        "rnbq1rk1/4bppp/1pn1p3/pN1pP3/P2P4/5N2/1P3PPP/R1BQKB1R",
                        //Attacking the b5 square
                        "rnbq1rk1/4bppp/1pn1p3/pN1pP3/P2P4/5N2/1P2BPPP/R1BQK2R",
                        "rn1q1rk1/4bppp/bpn1p3/pN1pP3/P2P4/5N2/1P2BPPP/R1BQK2R",
                        // Increasing pressure on b5 square
                        "rn1q1rk1/4bppp/bpn1p3/pN1pP3/P2P4/5N2/1P2BPPP/R1BQ1RK1",
                        "rn1q1rk1/n3bppp/bp2p3/pN1pP3/P2P4/5N2/1P2BPPP/R1BQ1RK1",
                        // Further increasing the pressure on b5 square
                        "rn1q1rk1/n3bppp/bp2p3/pN1pP3/P2P4/5N2/1P1BBPPP/R2Q1RK1",
                        "rn3rk1/n2qbppp/bp2p3/pN1pP3/P2P4/5N2/1P1BBPPP/R2Q1RK1",
                        // Start using our outpost on b4
                        "rn3rk1/n2qbppp/bp2p3/pN1pP3/P2P4/1Q3N2/1P1BBPPP/R4RK1",
                        "r4rk1/n2qbppp/bpn1p3/pN1pP3/P2P4/1Q3N2/1P1BBPPP/R4RK1",
                        // Fighting for c file
                        "r4rk1/n2qbppp/bpn1p3/pN1pP3/P2P4/1Q3N2/1P1BBPPP/R1R3K1",
                        "r1r3k1/n2qbppp/bpn1p3/pN1pP3/P2P4/1Q3N2/1P1BBPPP/R1R3K1",
                        // Using outpost for knight
                        "r1r3k1/n2qbppp/bpn1p3/pN1pP3/P2P4/1Q3N2/1P1B1PPP/R1R2BK1",
                        "r1r3k1/n2qbppp/bp2p3/pN1pP3/Pn1P4/1Q3N2/1P1B1PPP/R1R2BK1",
                        //Recapturing rook
                        "r1R3k1/n2qbppp/bp2p3/pN1pP3/Pn1P4/1Q3N2/1P1B1PPP/R4BK1",
                        "r1b3k1/n2qbppp/1p2p3/pN1pP3/Pn1P4/1Q3N2/1P1B1PPP/R4BK1"

                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Preparing d5",
                        "",
                        "Fighting for the center",
                        "",
                        "Creating pawn breaktrough",
                        "",
                        "Preparing Nec6",
                        "",
                        "Kingside knight found his dream square",
                        "",
                        "Stopping b4 for good",
                        "",
                        "Connecting the pawns",
                        "",
                        "Try to create outpost on b4",
                        "",
                        "Developing a piece",
                        "",
                        "Getting the king safe",
                        "",
                        "Attacking the b5 square",
                        "",
                        "Increasing pressure on b5 square",
                        "",
                        "Further increasing the pressure on b5 square",
                        "",
                        "Start using our outpost on b4",
                        "",
                        "Fighting for c file",
                        "",
                        "Using outpost for knight",
                        "",
                        "Recapturing rook",




                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Second Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/pp3ppp/4p3/2ppP3/3P4/8/PPP2PPP/RNBQKBNR"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN",
                        "Comment on the third FEN",
                        "Comment on the fourth FEN",
                        "Comment on the fifth FEN",
                        "Comment on the sixth FEN",

                        )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Third Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/pp3ppp/4p3/2ppP3/3P4/8/PPP2PPP/RNBQKBNR"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN",
                        "Comment on the third FEN",
                        "Comment on the fourth FEN",
                        "Comment on the fifth FEN",
                        "Comment on the sixth FEN",

                        )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Fourth Variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/pp3ppp/4p3/2ppP3/3P4/8/PPP2PPP/RNBQKBNR"
                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "Comment on the first FEN",
                        "Comment on the second FEN",
                        "Comment on the third FEN",
                        "Comment on the fourth FEN",
                        "Comment on the fifth FEN",
                        "Comment on the sixth FEN",

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