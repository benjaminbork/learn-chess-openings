package com.example.learnchessopenings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.user
import com.example.learnchessopenings.Models.variation
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate


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
    private lateinit var reviewLearnNavBarItems : BottomNavigationView
    private lateinit var  reviewLearnNavBar : View
    private lateinit var returnAppBar : Toolbar

    private lateinit var loadingDialog : View

    private var courseId = 0
    private lateinit var chessCourse : Map<String, Any>
    private lateinit var chessVariations : MutableList<*>
    private var chessVariationsIndex : Int = -1
    private var chessVariationsLength : Int = -1
    private var hasDateSet = false
    private var reviewedLength = 0





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
        reviewLearnNavBar = findViewById(R.id.learnReviewNavBar)
        reviewLearnNavBarItems = findViewById(R.id.reviewLearnNavigationView)
        returnAppBar = findViewById(R.id.return_app_bar)
        loadingDialog = findViewById(R.id.loadingDialogInclude)

        db = DbHelper(this)

        courseId = intent.getIntExtra("courseId", 0)
        chessCourse = course.getCourse(courseId, db)
        
        var chessVariationIDs = intent.getStringExtra("variations")

        if (chessVariationIDs != null) {
            chessVariationIDs = chessVariationIDs.dropLast(2)
            chessVariations = chessVariationIDs.split(", ").toMutableList()
        }

        chessVariationsIndex = 0
        chessVariationsLength = chessVariations.size




        loadingDialog.isVisible = false
        chessView.isVisible = true
        chessHeader.isVisible = true
        reviewNavBar.isVisible = true

        returnAppBar.title = chessCourse["title"].toString()

        reviewNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.solution -> {
                    if (!hasDateSet) {
                        variation.setDate(
                            chessVariations[chessVariationsIndex].toString().toInt(),
                            db,
                            LocalDate.now()
                        )
                    }
                    increaseReviewIndex()
                    chessAlert.text = "You did not solve it."
                    chessAlert.isVisible = true
                    chessModel.setReviewInactive()
                    chessModel.stopGame()
                    reviewLearnNavBar.isVisible = true
                    reviewNavBar.isVisible = false
                    chessView.invalidate()
                }
            }
            true
        }

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

        reviewLearnNavBarItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.previous -> {
                    showPreviousPosition()
                }
                R.id.next -> {
                    showNextPosition()
                }
                R.id.nextReview -> {
                    if(!checkReviewDone()) {
                        reviewLearnNavBar.isVisible = false
                        chessAlert.isVisible = false
                        chessSubHeader.isVisible = false
                        chessModel.reset()
                        chessModel.resetReview()
                        loadVariation()
                        hasDateSet = false
                        chessView.invalidate()
                    } else {
                        chessAlert.isVisible = true
                        chessAlert.text = "Nothing new to review."
                    }

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

        loadVariation()


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

    // puzzle functions (needed for chessView)

    override fun hasPuzzleMoveMade(): Boolean {
        return false
    }

    override fun isPuzzleActive(): Boolean {
        return chessModel.isPuzzleActive()
    }


    // review functions
    private fun showNextPosition() {
        chessModel.increaseReviewIndex()
        chessModel.setReviewPosition()
        chessSubHeader.isVisible = true
        chessSubHeader.text = chessModel.getComment()
        chessView.invalidate()
    }

    private fun showPreviousPosition() {
        chessModel.decreaseReviewIndex()
        chessModel.setReviewPosition()
        chessSubHeader.isVisible = true
        chessSubHeader.text = chessModel.getComment()
        chessView.invalidate()
    }


    private fun loadVariation () {
        val variationIndex = chessVariations[chessVariationsIndex].toString().toInt()
        val chessVariation = variation.getVariation(variationIndex, db)
        val playerToMove = chessCourse["black"].toString().toInt()
        var variationFensString = chessVariation["fen"].toString()
        chessHeader.text = chessVariation["title"].toString()
        variationFensString = variationFensString.replace("[", "")
        variationFensString = variationFensString.replace("]", "")
        val variationFens  = variationFensString.split(", ") as MutableList<String>
        var commentsString = chessVariation["comments"].toString()
        commentsString = commentsString.replace("[", "")
        commentsString = commentsString.replace("]", "")
        val comments = commentsString.split(", ") as MutableList<String>
        reviewedLength += 1
        chessModel.setReviewActive()
        chessModel.startGame()
        chessModel.resetReview()
        chessModel.setVariationData(playerToMove, variationFens, comments)
        if (playerToMove == 1) {
            chessModel.setReviewPosition()
            chessModel.increaseReviewIndex()
        }
    }



    //review functions
    override fun isReviewActive(): Boolean {
        return chessModel.isReviewActive()
    }

    override fun hasReviewMoveMade(): Boolean {
        return chessModel.hasReviewMoveMade()
    }
    
    private fun increaseReviewIndex() {
        if (chessVariationsIndex < chessVariationsLength - 1) {
            chessVariationsIndex += 1
        }
    }


    private fun checkReviewDone() : Boolean{
        return (chessVariationsLength == reviewedLength)
    }

    override fun checkIsMoveCorrect() {
        if (!hasDateSet) {
            variation.setDate(chessVariations[chessVariationsIndex].toString().toInt(),db, LocalDate.now())
            hasDateSet = true
        }

        if (chessModel.isReviewMoveCorrect() && chessModel.isReviewCompleted()) {
            increaseReviewIndex()
            chessAlert.text = "+10 XP"
            user.addExp(10, db)
            chessAlert.isVisible = true
            chessModel.stopGame()
            chessModel.setReviewInactive()
            
            
            reviewLearnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            chessView.invalidate()
        } else if (chessModel.isReviewMoveCorrect() && !chessModel.isReviewCompleted()) {
            increaseReviewIndex()
            chessModel.increaseReviewIndex()
            chessModel.setReviewPosition()
            chessModel.increaseReviewIndex()
            chessView.invalidate()
        } else {
            increaseReviewIndex()
            chessAlert.text = "That was not the right solution."
            chessAlert.isVisible = true
            chessModel.setReviewInactive()
            chessModel.stopGame()
            reviewLearnNavBar.isVisible = true
            reviewNavBar.isVisible = false
            chessView.invalidate()
        }
    }

}