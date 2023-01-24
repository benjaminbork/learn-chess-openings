package com.example.learnchessopenings

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.example.learnchessopenings.R.id.chessView


const val TAG = "ChessActivity"
class ChessActivity : AppCompatActivity(), ChessDelegate{
    var chessModel = ChessModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chess_screen)
        Log.d(TAG,chessModel.toString())
        findViewById<ChessView>(chessView).chessDelegate = this
    }
    override fun pieceAt(col: Int, row: Int): ChessPiece? {
        return chessModel.pieceAt(col,row)
    }

    override fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        chessModel.movePiece(fromCol, fromRow, toCol, toRow)
        findViewById<ChessView>(chessView).invalidate()
    }

}