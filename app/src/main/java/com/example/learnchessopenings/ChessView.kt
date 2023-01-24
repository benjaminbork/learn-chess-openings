package com.example.learnchessopenings

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap


class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val cellSide : Float = 130F
    private val originX : Float = 20F
    private val originY : Float = 200F
    private val paint = Paint()
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
    override fun onDraw(canvas: Canvas?) {
        loadBitMaps()
        drawChessBoard(canvas)
        drawPieces(canvas)
    }

    private fun loadBitMaps() {
        svgResIDs.forEach {
            bitmaps[it] = AppCompatResources.getDrawable(context, it)?.toBitmap()
        }

    }

    private fun drawPieces(canvas: Canvas?) {
        for (row in 0..7) {
            for (col in 7 downTo 0) {
                chessDelegate?.pieceAt(col,row)?.let { drawPieceAt(canvas,col,row, it.resID) }
            }
        }
    }

    private fun drawPieceAt(canvas: Canvas?, col: Int, row: Int, resID: Int) {
        val bitmap = bitmaps[resID] !!
        canvas?.drawBitmap(bitmap,null, RectF(originX + col * cellSide, originY + (7 - row) * cellSide ,originX + (col + 1) * cellSide,originY + (7 - row + 1) * cellSide),paint)
    }

    private fun drawChessBoard(canvas: Canvas?) {
        for (row in 0..7 ) {
            for (col in 0..7) {
                drawSquareAt(canvas,row,col,((row + col) % 2 == 0))
            }
        }
    }

    private fun drawSquareAt(canvas: Canvas?, col: Int,row: Int,isDark: Boolean) {
        paint.color = if (isDark) Color.WHITE else resources.getColor(R.color.primary_green)
        canvas?.drawRect(originX +  col * cellSide,originY + row * cellSide, originX + (col + 1) * cellSide, originY + (row + 1) * cellSide, paint)
    }

}