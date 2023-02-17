package com.example.learnchessopenings

import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.variation

import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.HttpException
import java.io.IOException
import kotlin.properties.Delegates


class LearnActivity : AppCompatActivity(), ChessDelegate{
    private var db: DbHelper = DbHelper(this)
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
    private lateinit var fens : MutableList<*>
    private lateinit var comments : MutableList<*>
    private var variationId = 0
    private var variationLength = -1
    private var variationIndex = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chess_screen)
        chessView = findViewById(R.id.chessView)
        chessView.chessDelegate = this

        chessHeader = findViewById(R.id.chessHeader)
        chessSubHeader = findViewById(R.id.chessSubHeader)
        learnNavBarItems = findViewById(R.id.learnNavigationView)
        learnNavBar = findViewById(R.id.learnNavBar)
        returnAppBar = findViewById(R.id.return_app_bar)
        loadingDialog = findViewById(R.id.loadingDialogInclude)

        db = DbHelper(this)

        chessModel.stopGame()


        variationId = intent.getIntExtra("id", 0)
        val courseTitle = intent.getStringExtra("courseTitle")
        val coursePlayer = intent.getIntExtra("coursePlayer", 0)
        val variation = variation.getVariation(variationId,db)
        fens = variation["fen"] as MutableList<*>
        comments = variation["comments"] as MutableList<*>
        variationLength = fens.size

        returnAppBar.title = "$courseTitle"
        chessHeader.text = variation["title"].toString()
        loadingDialog.isVisible = false
        chessView.isVisible = true
        chessHeader.isVisible = true
        learnNavBar.isVisible = true

        learnNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.previous -> {
                   showPreviousPosition()
                }
                R.id.next -> {
                    showNextPosition()
                }
            }
            true
        }


        returnAppBar.setNavigationOnClickListener{
            finish()
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

    // learn functions

    private fun showNextPosition() {
        increaseIndex()
        if (!chessSubHeader.isVisible) {
            chessSubHeader.isVisible = true
        }

        if (variationIndex != -1 && variationIndex <= variationLength - 1) {
            chessModel.loadFEN(fens[variationIndex] as String)
            chessSubHeader.text = comments[variationIndex] as String
            chessView.invalidate()
        }

        if (variationIndex != -1 && variationIndex == variationLength -1) {
            setVariationLearned()
        }

    }

    private fun showPreviousPosition() {
        decreaseIndex()

        if (variationIndex != -1) {
            chessModel.loadFEN(fens[variationIndex] as String)
            chessSubHeader.text = comments[variationIndex] as String
            chessView.invalidate()
        }

    }

    private fun increaseIndex () {
        val tempVariationIndex = variationIndex + 1
        if (variationIndex <= variationLength - 1) variationIndex = tempVariationIndex
    }

    private fun decreaseIndex () {
        val tempVariationIndex = variationIndex - 1
        if (variationIndex > 0) variationIndex = tempVariationIndex
    }

    private fun setVariationLearned() {
        variation.setLearned(db,variationId)
        chessHeader.text = "learned"
    }

    // puzzle function ( needed for chessView)
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