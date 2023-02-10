package com.example.learnchessopenings

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.learnchessopenings.R.id.chess

import com.example.learnchessopenings.R.id.chessView


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chess_screen)
        chessView = findViewById<ChessView>(R.id.chessView)
        chessModel.reset()
        chessView.chessDelegate = this
        findViewById<Button>(R.id.reset_btn).setOnClickListener {
            /*if (i < exampleGame.size) {
                chessModel.loadFEN(exampleGame[i])
                i += 1
            } else {
                findViewById<TextView>(R.id.textView).text = "DONE"
            } */
            movePiece(chessModel.stringToChessSquare("a1"), chessModel.stringToChessSquare("h8"))
            chessView.invalidate()
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

    override fun canPieceMove(from: ChessSquare, to: ChessSquare): Boolean {
        return chessModel.canPieceMove(from,to)
    }

    override fun resetCheckingPieces() {
        chessModel.resetCheckingPieces()
    }

}