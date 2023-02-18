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
            course.Course.COLUMN_NAME_DESCRIPTION to "Solid choice for black against 1.e4",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable._7099,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Advanced variation",
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
                        "Recapturing rook"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Exchange variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Preparing d5
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Fighting for the center
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        // Exchanging center pawns
                        "rnbqkbnr/ppp2ppp/4p3/3P4/3P4/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/8/3p4/3P4/8/PPP2PPP/RNBQKBNR",
                        // Attacking center pawn
                        "rnbqkbnr/ppp2ppp/8/3p4/3P4/3B4/PPP2PPP/RNBQK1NR",
                        "r1bqkbnr/ppp2ppp/2n5/3p4/3P4/3B4/PPP2PPP/RNBQK1NR",
                        // Developing the bishop to active square
                        "r1bqkbnr/ppp2ppp/2n5/3p4/3P4/2PB4/PP3PPP/RNBQK1NR",
                        "r1bqk1nr/ppp2ppp/2nb4/3p4/3P4/2PB4/PP3PPP/RNBQK1NR",
                        // Fighting for central squares
                        "r1bqk1nr/ppp2ppp/2nb4/3p4/3P4/2PB4/PP2NPPP/RNBQK2R",
                        "r1b1k1nr/ppp2ppp/2nb1q2/3p4/3P4/2PB4/PP2NPPP/RNBQK2R",
                        // Develop bishop to active square
                        "r1b1k1nr/ppp2ppp/2nb1q2/3p4/3P4/2PB4/PP1NNPPP/R1BQK2R",
                        "r3k1nr/ppp2ppp/2nb1q2/3p1b2/3P4/2PB4/PP1NNPPP/R1BQK2R",
                        // Preventing knight jumps
                        "r3k1nr/ppp2ppp/2nb1q2/3p1b2/3P4/2PB1N2/PP2NPPP/R1BQK2R",
                        "r3k1nr/ppp2pp1/2nb1q1p/3p1b2/3P4/2PB1N2/PP2NPPP/R1BQK2R",
                        // Exchange his good bishop
                        "r3k1nr/ppp2pp1/2nb1q1p/3p1b2/3P1B2/2PB1N2/PP2NPPP/R2QK2R",
                        "r3k1nr/ppp2pp1/2nb1q1p/3p4/3P1B2/2Pb1N2/PP2NPPP/R2QK2R",
                        // Retreating our good bishop
                        "r3k1nr/ppp2pp1/2nB1q1p/3p4/3P4/2Pb1N2/PP2NPPP/R2QK2R",
                        "r3k1nr/ppp2pp1/2nB1q1p/3p4/3Pb3/2P2N2/PP2NPPP/R2QK2R",
                        // Ruining his kingside structure
                        "r3k1nr/ppB2pp1/2n2q1p/3p4/3Pb3/2P2N2/PP2NPPP/R2QK2R",
                        "r3k1nr/ppB2pp1/2n2q1p/3p4/3P4/2P2b2/PP2NPPP/R2QK2R",
                        // Winning our pawn back
                        "r3k1nr/ppB2pp1/2n2q1p/3p4/3P4/2P2P2/PP2NP1P/R2QK2R",
                        "r3k1nr/ppB2pp1/2n4p/3p4/3P4/2P2q2/PP2NP1P/R2QK2R",

                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Preparing d5",
                        "",
                        "Fighting for the center",
                        "",
                        "Exchanging center pawns",
                        "",
                        "Attacking center pawn",
                        "",
                        "Developing the bishop to active square",
                        "",
                        "Fighting for central squares",
                        "",
                        "Develop bishop to active square ",
                        "",
                        "Preventing knight jumps",
                        "",
                        "Exchanging his good bishop",
                        "",
                        "Retreating our good bishop",
                        "",
                        "Ruining his kingside structure",
                        "",
                        "Winning our pawn back"
                        )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Classical variation",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Preparing d5
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Fighting for the center
                        "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR",
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR",
                        // Developing knight to active square
                        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/2N5/PPP2PPP/R1BQKBNR",
                        "rnbqkb1r/ppp2ppp/4pn2/3p4/3PP3/2N5/PPP2PPP/R1BQKBNR",
                        // Removing the pin
                        "rnbqkb1r/ppp2ppp/4pn2/3p2B1/3PP3/2N5/PPP2PPP/R2QKBNR",
                        "rnbqk2r/ppp1bppp/4pn2/3p2B1/3PP3/2N5/PPP2PPP/R2QKBNR",
                        // Saving the knight
                        "rnbqk2r/ppp1bppp/4pn2/3pP1B1/3P4/2N5/PPP2PPP/R2QKBNR",
                        "rnbqk2r/pppnbppp/4p3/3pP1B1/3P4/2N5/PPP2PPP/R2QKBNR",
                        // Attacking the bishop
                        "rnbqk2r/pppnbppp/4p3/3pP1B1/3P3P/2N5/PPP2PP1/R2QKBNR",
                        "rnbqk2r/pppnbpp1/4p2p/3pP1B1/3P3P/2N5/PPP2PP1/R2QKBNR",
                        // Capturing the bishop
                        "rnbqk2r/pppnBpp1/4p2p/3pP3/3P3P/2N5/PPP2PP1/R2QKBNR",
                        "rnb1k2r/pppnqpp1/4p2p/3pP3/3P3P/2N5/PPP2PP1/R2QKBNR",
                        // Defending the pawn
                        "rnb1k2r/pppnqpp1/4p2p/3pP3/3P2QP/2N5/PPP2PP1/R3KBNR",
                        "rnb2rk1/pppnqpp1/4p2p/3pP3/3P2QP/2N5/PPP2PP1/R3KBNR",
                        // Counter attacking the center
                        "rnb2rk1/pppnqpp1/4p2p/3pP3/3P1PQP/2N5/PPP3P1/R3KBNR",
                        "rnb2rk1/pp1nqpp1/4p2p/2ppP3/3P1PQP/2N5/PPP3P1/R3KBNR"


                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Preparing d5",
                        "",
                        "Fighting for the center",
                        "",
                        "Developing knight to active square",
                        "",
                        "Removing the pin",
                        "",
                        "Saving the knight",
                        "",
                        "Attacking the bishop",
                        "",
                        "Capturing the bishop",
                        "",
                        "Defending the pawn",
                        "",
                        "Counter attacking the center"
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