package com.example.learnchessopenings

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val cellSide : Float = 130F
    private val originX : Float = 20F
    private val originY : Float = 200F

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()

        for (i in 0..7 ) {
            for (j in 0..7) {
                paint.color = if ((i + j) % 2 == 0) Color.LTGRAY else Color.DKGRAY
                canvas?.drawRect(originX +  i * cellSide,originY + j * cellSide, originX + (i + 1) * cellSide, originY + (j + 1) * cellSide, paint)

            }
        }


    }
}