package com.example.learnchessopenings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learnchessopenings.Models.dailyPuzzle
import com.example.learnchessopenings.Models.user

import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Text
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate


const val TAG = "ChessActivity"
class PuzzleActivity : AppCompatActivity(), ChessDelegate{
    private var db: DbHelper = DbHelper(this)
    var chessModel = ChessModel()
    private lateinit var chessView : ChessView
    private lateinit var chessHeader : TextView
    private lateinit var chessSubHeader : TextView
    private lateinit var chessAlert : TextView
    private lateinit var learnNavBarItems : BottomNavigationView
    private lateinit var learnNavBar : View
    private lateinit var reviewNavBarItems : BottomNavigationView
    private lateinit var reviewNavBar : View
    private lateinit var returnAppBar : Toolbar
    private lateinit var loadingDialog : View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chess_screen)
        chessView = findViewById(R.id.chessView)
        chessView.chessDelegate = this

        db = DbHelper(this)

        chessHeader = findViewById(R.id.chessHeader)
        chessSubHeader = findViewById(R.id.chessSubHeader)
        chessAlert = findViewById(R.id.chessAlert)
        learnNavBarItems = findViewById(R.id.learnNavigationView)
        learnNavBar = findViewById(R.id.learnNavBar)
        reviewNavBarItems = findViewById(R.id.reviewNavigationView)
        reviewNavBar = findViewById(R.id.reviewNavBar)
        returnAppBar = findViewById(R.id.return_app_bar)
        loadingDialog = findViewById(R.id.loadingDialogInclude)

        returnAppBar.title = "Daily Puzzle"



        learnNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.previous -> {
                    showPreviousPuzzlePosition()
                }
                R.id.next -> {
                    showNextPuzzlePosition()

                }
            }
            true
        }

        reviewNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.solution -> {
                    reviewNavBar.isVisible = false
                    learnNavBar.isVisible = true
                    chessModel.setPuzzleInactive()
                    chessModel.stopGame()
                    chessView.invalidate()
                    // TODO add xp
                    if (!hasPuzzleBeenPlayedToday()) {
                        dailyPuzzle.setDate(db, LocalDate.now())
                    }
                }
            }
            true
        }
        returnAppBar.setNavigationOnClickListener{
            finish()
        }

        // load puzzle
        lifecycleScope.launchWhenCreated {

            val response = try {
                RetrofitInstance.api.getData()
            } catch (e: IOException) {
                chessModel.stopGame()
                chessView.isVisible = true
                chessAlert.isVisible = true
                chessAlert.text = "Something went wrong. \nCheck your internet connection..."
                return@launchWhenCreated
            } catch (e: HttpException) {
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                chessModel.setPuzzleData(response)
                chessModel.loadPuzzleStartingPosition()
                chessModel.loadPuzzlePositions()
                chessModel.setPuzzleActive()
                chessModel.increasePuzzleIndex()
                var i = 0
                while (i < 10 && !chessModel.checkPuzzleLoaded()) {
                     i += 1
                 }
                if (chessModel.checkPuzzleLoaded()) {
                    reviewNavBar.isVisible = true
                    chessView.isVisible = true
                    chessHeader.text = chessModel.getPuzzlePlayerToMove()
                    chessHeader.isVisible = true
                }

                if (hasPuzzleBeenPlayedToday()) {
                    chessAlert.text = "You already played this puzzle."
                    reviewNavBar.isVisible = false
                    learnNavBar.isVisible = true
                    chessAlert.isVisible = true
                    chessHeader.isVisible = false
                    chessModel.stopGame()
                }

                loadingDialog.isVisible = false
            } else {
                reviewNavBar.isVisible = true
                chessView.isVisible = true
                chessModel.stopGame()
                chessAlert.text = "Something went wrong..."
            }
        }
        chessView.invalidate()

    }

    // general functions
    override fun pieceAt(square: ChessSquare): ChessPiece? {
        return chessModel.pieceAt(square)
    }

    override fun movePiece(from: ChessSquare, to: ChessSquare) {
        chessModel.movePiece(from, to)
        chessView.invalidate()
    }

    override fun toFen(): String {
        return chessModel.toFen()
    }

    override fun stringToChessSquare(squareString: String): ChessSquare {
        return chessModel.stringToChessSquare(squareString)
    }

    override fun getValidMovesForView(): MutableList<ChessMove> {
        return chessModel.getValidMovesForView()
    }

    override fun isPuzzleActive(): Boolean {
        return chessModel.isPuzzleActive()
    }

    // puzzle functions

    private fun showNextPuzzlePosition() {
        chessModel.increasePuzzleIndex()
        chessModel.setPuzzlePosition()
        chessView.invalidate()
    }

    private fun showPreviousPuzzlePosition() {
        chessModel.decreasePuzzleIndex()
        chessModel.setPuzzlePosition()
        chessView.invalidate()
    }

    override fun checkIsMoveCorrect() {
        if (chessModel.isMoveCorrect() && chessModel.isPuzzleCompleted()) {
            chessAlert.isVisible = true
            chessModel.stopGame()
            chessModel.setPuzzleInactive()
            learnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            chessView.invalidate()
            if (!hasPuzzleBeenPlayedToday()) {
                dailyPuzzle.setDate(db, LocalDate.now())
                chessAlert.text = "+10 XP"
                user.addExp(10, db)
            }
        } else if (chessModel.isMoveCorrect() && !chessModel.isPuzzleCompleted()) {
            chessModel.increasePuzzleIndex()
            chessModel.setPuzzlePosition()
            chessModel.increasePuzzleIndex()
            chessView.invalidate()
        } else {
            chessAlert.text = "That was not the right solution."
            chessAlert.isVisible = true
            chessModel.setPuzzleInactive()
            chessModel.stopGame()
            learnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            if (!hasPuzzleBeenPlayedToday()) {
                dailyPuzzle.setDate(db, LocalDate.now())
            }
            chessView.invalidate()
        }


    }

    private fun hasPuzzleBeenPlayedToday() : Boolean{
        return (dailyPuzzle.getDailyDate(db) == LocalDate.now())
    }

    override fun hasPuzzleMoveMade(): Boolean {
        return chessModel.hasPuzzleMoveMade()
    }

    // review functions for chessView needed
    override fun isReviewActive(): Boolean {
        return false
    }

    override fun hasReviewMoveMade(): Boolean {
        return false
    }


}