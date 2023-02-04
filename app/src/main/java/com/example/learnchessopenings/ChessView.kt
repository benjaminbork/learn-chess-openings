package com.example.learnchessopenings

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import java.lang.Integer.min


class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val scaleFactor = .9f
    private var cellSide : Float = 130f
    private var originX : Float = 20f
    private var originY : Float = 200f
    private val paint = Paint()
    private var fromCol : Int = -1
    private var fromRow : Int = -1
    private var movingPieceX : Float = -1f
    private var movingPieceY : Float = -1f
    private var movingPieceBitMap : Bitmap? = null
    private  var movingPiece : ChessPiece? = null

    private val svgResIDs = setOf(
        R.drawable.bq,
        R.drawable.bk,
        R.drawable.br,
        R.drawable.bb,
        R.drawable.bn,
        R.drawable.bp,
        R.drawable.wq,
        R.drawable.wk,
        R.drawable.wr,
        R.drawable.wb,
        R.drawable.wn,
        R.drawable.wp,
    )
    private val bitmaps = mutableMapOf<Int, Bitmap?>()

    var chessDelegate : ChessDelegate? = null

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val smaller = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(smaller, smaller)
    }
    override fun onDraw(canvas: Canvas?) {
        canvas?: return
        loadBitMaps()
        val chessBoardSide = min(width, height) * scaleFactor
        cellSide = chessBoardSide / 8f
        originX = (width - chessBoardSide) / 2
        originY = (height - chessBoardSide) / 6
        if (movingPiece == null) {
            drawChessBoard(canvas)
        } else {
            drawChessBoardWithHeatMap(canvas)
        }
        drawPieces(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = 7 - ((event.y - originY) / cellSide).toInt()
                chessDelegate?.pieceAt(ChessSquare(fromCol,fromRow))?.let {
                    movingPiece = it
                    movingPieceBitMap = bitmaps[it.resID]
                }

            }

            MotionEvent.ACTION_MOVE -> {
                movingPieceX = event.x
                movingPieceY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val col = ((event.x - originX) / cellSide).toInt()
                val row = 7 - ((event.y - originY) / cellSide).toInt()
                chessDelegate?.movePiece(ChessSquare(fromCol,fromRow), ChessSquare(col,row))
                movingPieceBitMap = null
                movingPiece = null
            }
        }
        return true
    }
    private fun loadBitMaps() {
        svgResIDs.forEach {
            bitmaps[it] = AppCompatResources.getDrawable(context, it)?.toBitmap()
        }
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0..7) {
            for (col in 7 downTo 0) {
                chessDelegate?.pieceAt(ChessSquare(col,row))?.let {
                    if (it != movingPiece) {
                        drawPieceAt(canvas,col,row, it.resID)
                    }
                }
            }
        }

        movingPieceBitMap?.let {
            canvas.drawBitmap(it,null, RectF(movingPieceX - cellSide / 2, movingPieceY - cellSide / 2 ,movingPieceX + cellSide / 2,movingPieceY + cellSide / 2),paint)
        }
    }

    private fun drawPieceAt(canvas: Canvas, col: Int, row: Int, resID: Int) {
        val bitmap = bitmaps[resID] !!
        canvas.drawBitmap(bitmap,null, RectF(originX + col * cellSide, originY + (7 - row) * cellSide ,originX + (col + 1) * cellSide,originY + (7 - row + 1) * cellSide),paint)
    }

    private fun drawChessBoard(canvas: Canvas) {
        for (row in 0..7 ) {
            for (col in 0..7) {
                drawSquareAt(canvas,row,col,((row + col) % 2 == 0))
            }
        }
    }

    private fun drawChessBoardWithHeatMap(canvas: Canvas) {
        var isPossibleMove : Boolean?
        for (row in 0..7 ) {
            for (col in 0..7) {
                isPossibleMove =movingPiece?.let { chessDelegate?.canPieceMove(ChessSquare(it.col,it.row), ChessSquare(col, row)) }
                drawSquareAtWithHeatMap(canvas,col,row,((row + col) % 2 == 0), isPossibleMove)
            }


        }
    }


    private fun drawSquareAt(canvas: Canvas, col: Int,row: Int,isDark: Boolean) {
        paint.color = if (isDark) Color.WHITE else resources.getColor(R.color.primary_green)
        canvas.drawRect(originX +  col * cellSide,originY + row * cellSide, originX + (col + 1) * cellSide, originY + (row + 1) * cellSide, paint)
    }

    private fun drawSquareAtWithHeatMap(canvas: Canvas, col: Int, row: Int, isDark: Boolean, isPossibleMove: Boolean?
    ) {
        paint.color = if (isPossibleMove == true && isDark)  ColorUtils.blendARGB(Color.RED, Color.WHITE,0.3f)else if (isPossibleMove == true) ColorUtils.blendARGB(Color.RED, Color.WHITE,0.4f)  else if (isDark) resources.getColor(R.color.primary_green)  else Color.WHITE
        canvas.drawRect(RectF(originX + col * cellSide, originY + (7 - row) * cellSide ,originX + (col + 1) * cellSide,originY + (7 - row + 1) * cellSide),paint)
    }

}