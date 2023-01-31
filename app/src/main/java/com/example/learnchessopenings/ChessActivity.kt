package com.example.learnchessopenings

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.learnchessopenings.R.id.chess

import com.example.learnchessopenings.R.id.chessView


const val TAG = "ChessActivity"
class ChessActivity : AppCompatActivity(), ChessDelegate{
    var chessModel = ChessModel()
    private lateinit var chessView : ChessView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chess_screen)
        chessView = findViewById<ChessView>(R.id.chessView)
        Log.d(TAG,chessModel.toString())
        chessView.chessDelegate = this
        findViewById<Button>(R.id.reset_btn).setOnClickListener {
            chessModel.loadFEN("rnbqkb1r/1ppppppp/p4n2/8/2P5/8/PP1PPPPP/RNBQKBNR")
            chessView.invalidate()
        }

    }
    override fun pieceAt(col: Int, row: Int): ChessPiece? {
        return chessModel.pieceAt(col,row)
    }

    override fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        chessModel.movePiece(fromCol, fromRow, toCol, toRow)
        chessView.invalidate()
    }

    override fun toFen(): String {
        return chessModel.toFen()
    }

}