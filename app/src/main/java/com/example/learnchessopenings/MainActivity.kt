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
            course.Course.COLUMN_NAME_TITLE to "Sicilian",
            course.Course.COLUMN_NAME_BLACK to 1,
            course.Course.COLUMN_NAME_DESCRIPTION to "Aggressive choice for black against 1.e4",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable._7098,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Accelerated Dragon",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Preparing kingside fianchetto
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R",
                        "rnbqkbnr/pp1ppp1p/6p1/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R",
                        // Capturing central pawn
                        "rnbqkbnr/pp1ppp1p/6p1/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R",
                        "rnbqkbnr/pp1ppp1p/6p1/8/3pP3/5N2/PPP2PPP/RNBQKB1R",
                        // Attacking central pawn
                        "rnbqkbnr/pp1ppp1p/6p1/8/3NP3/8/PPP2PPP/RNBQKB1R",
                        "rnbqkb1r/pp1ppp1p/5np1/8/3NP3/8/PPP2PPP/RNBQKB1R",
                        // Developing the knight
                        "rnbqkb1r/pp1ppp1p/5np1/8/3NP3/2N5/PPP2PPP/R1BQKB1R",
                        "r1bqkb1r/pp1ppp1p/2n2np1/8/3NP3/2N5/PPP2PPP/R1BQKB1R",
                        // Fianchetto the bishop
                        "r1bqkb1r/pp1ppp1p/2n2np1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R",
                        "r1bqk2r/pp1pppbp/2n2np1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R",
                        // Getting the king safe
                        "r1bqk2r/pp1pppbp/2n2np1/8/3NP3/2N1BP2/PPP3PP/R2QKB1R",
                        "r1bq1rk1/pp1pppbp/2n2np1/8/3NP3/2N1BP2/PPP3PP/R2QKB1R"



                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Preparing kingside fianchetto",
                        "",
                        "Capturing central pawn",
                        "",
                        "Attacking central pawn",
                        "",
                        "Developing the knight",
                        "",
                        "Fianchetto the bishop",
                        "",
                        "Getting the king safe"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Anti-Maroczy",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Preparing kingside fianchetto
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R",
                        "rnbqkbnr/pp1ppp1p/6p1/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R",
                        // Fianchetto the bishop
                        "rnbqkbnr/pp1ppp1p/6p1/2p5/2P1P3/5N2/PP1P1PPP/RNBQKB1R",
                        "rnbqk1nr/pp1pppbp/6p1/2p5/2P1P3/5N2/PP1P1PPP/RNBQKB1R",
                        // Pressuring the center
                        "rnbqk1nr/pp1pppbp/6p1/2p5/2PPP3/5N2/PP3PPP/RNBQKB1R",
                        "rnb1k1nr/pp1pppbp/6p1/q1p5/2PPP3/5N2/PP3PPP/RNBQKB1R",
                        // Opening up the light squared bishop
                        "rnb1k1nr/pp1pppbp/6p1/q1p5/2PPP3/2N2N2/PP3PPP/R1BQKB1R",
                        "rnb1k1nr/pp2ppbp/3p2p1/q1p5/2PPP3/2N2N2/PP3PPP/R1BQKB1R",
                        // Ruining his pawn structure
                        "rnb1k1nr/pp2ppbp/3p2p1/q1pP4/2P1P3/2N2N2/PP3PPP/R1BQKB1R",
                        "rnb1k1nr/pp2pp1p/3p2p1/q1pP4/2P1P3/2b2N2/PP3PPP/R1BQKB1R",
                        // Pinning his knight
                        "rnb1k1nr/pp2pp1p/3p2p1/q1pP4/2P1P3/2P2N2/P4PPP/R1BQKB1R",
                        "rn2k1nr/pp2pp1p/3p2p1/q1pP4/2P1P1b1/2P2N2/P4PPP/R1BQKB1R"

                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Preparing kingside fianchetto",
                        "",
                        "Fianchetto the bishop",
                        "",
                        "Pressuring the center",
                        "",
                        "Opening up the light squared bishop",
                        "",
                        "Ruining his pawn structure",
                        "",
                        "Pinning his knight"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Alapin",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR",
                        // Fighting for the center
                        "rnbqkbnr/pp1ppppp/8/2p5/4P3/2P5/PP1P1PPP/RNBQKBNR",
                        "rnbqkbnr/pp2pppp/8/2pp4/4P3/2P5/PP1P1PPP/RNBQKBNR",
                        // Centralizing the queen
                        "rnbqkbnr/pp2pppp/8/2pP4/8/2P5/PP1P1PPP/RNBQKBNR",
                        "rnb1kbnr/pp2pppp/8/2pq4/8/2P5/PP1P1PPP/RNBQKBNR",
                        // Preparing kingside fianchetto
                        "rnb1kbnr/pp2pppp/8/2pq4/3P4/2P5/PP3PPP/RNBQKBNR",
                        "rnb1kbnr/pp2pp1p/6p1/2pq4/3P4/2P5/PP3PPP/RNBQKBNR",
                        // Fianchetto the bishop
                        "rnb1kbnr/pp2pp1p/6p1/2pq4/3P4/2P2N2/PP3PPP/RNBQKB1R",
                        "rnb1k1nr/pp2ppbp/6p1/2pq4/3P4/2P2N2/PP3PPP/RNBQKB1R ",
                        // Saving the queen
                        "rnb1k1nr/pp2ppbp/6p1/2pq4/2PP4/5N2/PP3PPP/RNBQKB1R",
                        "rnb1k1nr/pp2ppbp/6p1/2p5/2PPq3/5N2/PP3PPP/RNBQKB1R",
                        // Opening the diagonal for our bishop
                        "rnb1k1nr/pp2ppbp/6p1/2p5/2PPq3/4BN2/PP3PPP/RN1QKB1R",
                        "rnb1k1nr/pp2ppbp/6p1/8/2Ppq3/4BN2/PP3PPP/RN1QKB1R"

                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Fighting for the center",
                        "",
                        "Centralizing the queen",
                        "",
                        "Preparing kingside fianchetto",
                        "",
                        "Fianchetto the bishop",
                        "",
                        "Saving the queen",
                        "",
                        "Opening the diagonal for our bishop"
                    )
                )
            )
        ),
        mapOf(
            course.Course.COLUMN_NAME_TITLE to "Dutch",
            course.Course.COLUMN_NAME_BLACK to 1,
            course.Course.COLUMN_NAME_DESCRIPTION to "Solid choice for black against 1.d4",
            course.Course.COLUMN_NAME_IMAGE_ID to R.drawable._7103,
            course.Course.COLUMN_NAME_VARIATIONS to arrayOf(
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Classical Dutch",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR",
                        "rnbqkbnr/ppppp1pp/8/5p2/3P4/8/PPP1PPPP/RNBQKBNR",
                        // Preparing square for bishop
                        "rnbqkbnr/ppppp1pp/8/5p2/3P4/5N2/PPP1PPPP/RNBQKB1R",
                        "rnbqkbnr/pppp2pp/4p3/5p2/3P4/5N2/PPP1PPPP/RNBQKB1R",
                        // Developing the knight
                        "rnbqkbnr/pppp2pp/4p3/5p2/3P4/5NP1/PPP1PP1P/RNBQKB1R",
                        "rnbqkb1r/pppp2pp/4pn2/5p2/3P4/5NP1/PPP1PP1P/RNBQKB1R",
                        // Developing the bishop
                        "rnbqkb1r/pppp2pp/4pn2/5p2/3P4/5NP1/PPP1PPBP/RNBQK2R",
                        "rnbqk2r/ppppb1pp/4pn2/5p2/3P4/5NP1/PPP1PPBP/RNBQK2R",
                        // Getting the king safe
                        "rnbqk2r/ppppb1pp/4pn2/5p2/3P4/5NP1/PPP1PPBP/RNBQ1RK1",
                        "rnbq1rk1/ppppb1pp/4pn2/5p2/3P4/5NP1/PPP1PPBP/RNBQ1RK1",
                        // Staying fluid in the center
                        "rnbq1rk1/ppppb1pp/4pn2/5p2/2PP4/5NP1/PP2PPBP/RNBQ1RK1",
                        "rnbq1rk1/ppp1b1pp/3ppn2/5p2/2PP4/5NP1/PP2PPBP/RNBQ1RK1",
                        // Stopping e4 push
                        "rnbq1rk1/ppp1b1pp/3ppn2/5p2/2PP4/2N2NP1/PP2PPBP/R1BQ1RK1",
                        "rnbq1rk1/ppp1b1pp/3pp3/5p2/2PPn3/2N2NP1/PP2PPBP/R1BQ1RK1"



                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Preparing square for bishop",
                        "",
                        "Developing the knight",
                        "",
                        "Developing the bishop",
                        "",
                        "Getting the king safe",
                        "",
                        "Staying fluid in the center",
                        "",
                        "Stopping e4 push"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "London System",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR",
                        "rnbqkbnr/ppppp1pp/8/5p2/3P4/8/PPP1PPPP/RNBQKBNR",
                        // Preparing square for bishop
                        "rnbqkbnr/ppppp1pp/8/5p2/3P4/5N2/PPP1PPPP/RNBQKB1R",
                        "rnbqkbnr/pppp2pp/4p3/5p2/3P4/5N2/PPP1PPPP/RNBQKB1R",
                        // Developing the knight
                        "rnbqkbnr/pppp2pp/4p3/5p2/3P1B2/5N2/PPP1PPPP/RN1QKB1R",
                        "rnbqkb1r/pppp2pp/4pn2/5p2/3P1B2/5N2/PPP1PPPP/RN1QKB1R",
                        // Preparing queenside fianchetto
                        "rnbqkb1r/pppp2pp/4pn2/5p2/3P1B2/4PN2/PPP2PPP/RN1QKB1R",
                        "rnbqkb1r/p1pp2pp/1p2pn2/5p2/3P1B2/4PN2/PPP2PPP/RN1QKB1R",
                        // Fianchetto the bishop
                        "rnbqkb1r/p1pp2pp/1p2pn2/5p2/3P1B2/3BPN2/PPP2PPP/RN1QK2R",
                        "rn1qkb1r/pbpp2pp/1p2pn2/5p2/3P1B2/3BPN2/PPP2PPP/RN1QK2R",
                        // Offering exchange of his good bishop
                        "rn1qkb1r/pbpp2pp/1p2pn2/5p2/3P1B2/3BPN2/PPPN1PPP/R2QK2R",
                        "rn1qk2r/pbpp2pp/1p1bpn2/5p2/3P1B2/3BPN2/PPPN1PPP/R2QK2R",
                        // Capturing his bishop
                        "rn1qk2r/pbpp2pp/1p1Bpn2/5p2/3P4/3BPN2/PPPN1PPP/R2QK2R",
                        "rn1qk2r/pb1p2pp/1p1ppn2/5p2/3P4/3BPN2/PPPN1PPP/R2QK2R"

                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Preparing square for bishop",
                        "",
                        "Developing the knight",
                        "",
                        "Preparing queenside fianchetto",
                        "",
                        "Fianchetto the bishop",
                        "",
                        "Offering exchange of his good bishop",
                        "",
                        "Capturing his bishop"
                    )
                ),
                mapOf(
                    variation.Variation.COLUMN_NAME_TITLE to "Reti",
                    variation.Variation.COLUMN_NAME_FEN to arrayOf(
                        // Fighting for the center
                        "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R",
                        "rnbqkbnr/ppppp1pp/8/5p2/8/5N2/PPPPPPPP/RNBQKB1R",
                        // Developing the knight
                        "rnbqkbnr/ppppp1pp/8/5p2/8/3P1N2/PPP1PPPP/RNBQKB1R",
                        "r1bqkbnr/ppppp1pp/2n5/5p2/8/3P1N2/PPP1PPPP/RNBQKB1R",
                        // Fighting for the center
                        "r1bqkbnr/ppppp1pp/2n5/5p2/4P3/3P1N2/PPP2PPP/RNBQKB1R",
                        "r1bqkbnr/pppp2pp/2n5/4pp2/4P3/3P1N2/PPP2PPP/RNBQKB1R",
                        // Preparing d5 push
                        "r1bqkbnr/pppp2pp/2n5/4pp2/4P3/2NP1N2/PPP2PPP/R1BQKB1R",
                        "r1bqkb1r/pppp2pp/2n2n2/4pp2/4P3/2NP1N2/PPP2PPP/R1BQKB1R",
                        // Getting the center
                        "r1bqkb1r/pppp2pp/2n2n2/4pP2/8/2NP1N2/PPP2PPP/R1BQKB1R",
                        "r1bqkb1r/ppp3pp/2n2n2/3ppP2/8/2NP1N2/PPP2PPP/R1BQKB1R",
                        // Preparing winning our pawn back
                        "r1bqkb1r/ppp3pp/2n2n2/3ppP2/3P4/2N2N2/PPP2PPP/R1BQKB1R",
                        "r1bqkb1r/ppp3pp/2n2n2/3p1P2/3p4/2N2N2/PPP2PPP/R1BQKB1R",
                        // Combination to win our pawn back
                        "r1bqkb1r/ppp3pp/2n2n2/3p1P2/3N4/2N5/PPP2PPP/R1BQKB1R",
                        "r1bqkb1r/ppp3pp/5n2/3p1P2/3n4/2N5/PPP2PPP/R1BQKB1R",
                        // Winning our pawn back
                        "r1bqkb1r/ppp3pp/5n2/3p1P2/3Q4/2N5/PPP2PPP/R1B1KB1R",
                        "r2qkb1r/ppp3pp/5n2/3p1b2/3Q4/2N5/PPP2PPP/R1B1KB1R"



                    ),
                    variation.Variation.COLUMN_NAME_COMMENTS to arrayOf(
                        "",
                        "Fighting for the center",
                        "",
                        "Developing the knight",
                        "",
                        "Fighting for the center",
                        "",
                        "Preparing d5 push",
                        "",
                        "Getting the center",
                        "",
                        "Preparing winning our pawn back",
                        "",
                        "Combination to win our pawn back",
                        "",
                        "Winning our pawn back"
                    )
                )
            )
        ),
    )
}