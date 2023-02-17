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
import org.w3c.dom.Text
import retrofit2.HttpException
import java.io.IOException
import kotlin.properties.Delegates


class ReviewActivity : AppCompatActivity(), ChessDelegate{
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

    private var courseId = 0
    private lateinit var chessCourse : Map<String, Any>
    private lateinit var chessVariations : MutableList<Int>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chess_screen)
        chessView = findViewById(R.id.chessView)
        chessView.chessDelegate = this

        chessHeader = findViewById(R.id.chessHeader)
        chessSubHeader = findViewById(R.id.chessSubHeader)
        chessAlert = findViewById(R.id.chessAlert)
        learnNavBarItems = findViewById(R.id.learnNavigationView)
        learnNavBar = findViewById(R.id.learnNavBar)
        reviewNavBar = findViewById(R.id.reviewNavBar)
        reviewNavBarItems = findViewById(R.id.reviewNavigationView)
        returnAppBar = findViewById(R.id.return_app_bar)
        loadingDialog = findViewById(R.id.loadingDialogInclude)

        db = DbHelper(this)

        courseId = intent.getIntExtra("courseId", 0)

        chessCourse = course.getCourse(courseId, db)
        Log.d(TAG, "onCreate: $chessCourse")




        loadingDialog.isVisible = false
        chessView.isVisible = true
        chessHeader.isVisible = true
        reviewNavBar.isVisible = true

        returnAppBar.title = chessCourse["title"].toString()



        learnNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.previous -> {
                   //showPreviousPosition()
                }
                R.id.next -> {
                    //showNextPosition()
                }
            }
            true
        }


        returnAppBar.setNavigationOnClickListener{
            val intent = Intent(applicationContext, DetailedCourse::class.java)
            intent.putExtra("id", courseId)
            startActivity(intent)
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


    // review functions
     private fun showNextPosition() {
         //TODO
     }


     private fun showPreviousPosition() {
         //TODO
     }

     private fun increaseIndex () {
         // TODO
     }

     private fun decreaseIndex () {
        // TODO
     }

     private fun checkIsVariationLearned (chessVariation : Map<String, Any>) {
         if (chessVariation["learned"] == 1) {
             //TODO
         }

     }




    // puzzle function ( needed for chessView)
    override fun checkIsMoveCorrect() {
        return
    }
    override fun hasPuzzleMoveMade(): Boolean {
        return false
    }


}