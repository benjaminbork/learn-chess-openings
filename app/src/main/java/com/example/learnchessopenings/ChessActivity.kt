package com.example.learnchessopenings

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learnchessopenings.R.id.chess

import com.example.learnchessopenings.R.id.chessView
import com.example.learnchessopenings.databinding.ChessScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.HttpException
import java.io.IOException


const val TAG = "ChessActivity"
class ChessActivity : AppCompatActivity(), ChessDelegate{
    var chessModel = ChessModel()
    var exampleGame : Array<String> = arrayOf(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
        "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR",
        "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR",
        "rnb1kbnr/ppp1pppp/8/3q4/8/8/PPPP1PPP/RNBQKBNR",
        "rnb1kbnr/ppp1pppp/8/3q4/8/2N5/PPPP1PPP/R1BQKBNR"
    )
    var i = 0
    private lateinit var chessView : ChessView
    private lateinit var chessHeader : TextView
    private lateinit var chessSubHeader : TextView
    private lateinit var learnNavBarItems : BottomNavigationView
    private lateinit var learnNavBar : View
    private lateinit var reviewNavBarItems : BottomNavigationView
    private lateinit var reviewNavBar : View

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

        learnNavBar.isVisible = true


        learnNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.previous -> {
                    Log.d(TAG, "previous touched")
                }
                R.id.next -> {
                    Log.d(TAG, "next touched")
                }
            }
            true
        }

        reviewNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.solution -> {
                    Log.d(TAG, "solution touched")
                }
            }
            true
        }

        // load puzzle
        lifecycleScope.launchWhenCreated {
            // TODO add progress bar
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
                var i = 0
                while (i < 10 && !chessModel.checkPuzzleLoaded()) {
                     i += 1
                 }
                if (chessModel.checkPuzzleLoaded()) chessView.isVisible = true

                chessView.invalidate()
            } else {
                Log.d(TAG, "Request failed.")
            }
        }


    }
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




}