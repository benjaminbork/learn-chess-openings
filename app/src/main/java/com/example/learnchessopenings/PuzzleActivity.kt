package com.example.learnchessopenings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope

import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.HttpException
import java.io.IOException


const val TAG = "ChessActivity"
class PuzzleActivity : AppCompatActivity(), ChessDelegate{
    var chessModel = ChessModel()
    private lateinit var chessView : ChessView
    private lateinit var chessHeader : TextView
    private lateinit var chessSubHeader : TextView
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

        chessHeader = findViewById(R.id.chessHeader)
        chessSubHeader = findViewById(R.id.chessSubHeader)
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
                    Log.d(TAG, "solution touched")
                    reviewNavBar.isVisible = false
                    learnNavBar.isVisible = true
                    chessModel.setPuzzleInactive()
                    chessModel.stopGame()
                    chessView.invalidate()
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
                Log.e(TAG, "Check your internet connection", )
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "Unexpected Response", )
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Request successful")
                Log.d(TAG, "onCreate: ${response.body()}")
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
                loadingDialog.isVisible = false
            } else {
                Log.d(TAG, "Request failed.")
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
        chessHeader.text = "You got the puzzle right"
            chessModel.stopGame()
            chessModel.setPuzzleInactive()
            learnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            chessView.invalidate()
            // TODO add xp and disable puzzle for the current day
        } else if (chessModel.isMoveCorrect() && !chessModel.isPuzzleCompleted()) {
            chessModel.increasePuzzleIndex()
            chessModel.setPuzzlePosition()
            chessModel.increasePuzzleIndex()
            chessView.invalidate()
        } else {
            chessHeader.text = "That was not the right solution."
            chessModel.setPuzzleInactive()
            chessModel.stopGame()
            learnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            chessView.invalidate()
        }


    }

    override fun hasPuzzleMoveMade(): Boolean {
        return chessModel.hasPuzzleMoveMade()
    }


}