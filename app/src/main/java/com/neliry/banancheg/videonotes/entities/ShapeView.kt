package com.neliry.banancheg.videonotes.entities

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class ShapeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mFillPaint: Paint = Paint()
    private val mStrokePaint: Paint = Paint()
    private val rectF: RectF = RectF()
    var isFillColor: Boolean = true

    var shapeType = 0

    var strokeWidth = 0f
    var fillColor = Color.RED
    var strokeColor = Color.YELLOW

    override fun onDraw(canvas: Canvas) {


        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.color = strokeColor
        mStrokePaint.strokeWidth = strokeWidth
        mStrokePaint.strokeCap = Paint.Cap.ROUND

        if(isFillColor) {
            mFillPaint.style = Paint.Style.FILL
            mFillPaint.color = fillColor
            rectF.set(1f, 1f, (width-0.5).toFloat(), (height-0.5).toFloat())
            when (shapeType) {
                1 -> canvas.drawRect(rectF, mFillPaint)
                2 -> canvas.drawOval(rectF, mFillPaint)
                else -> { // Note the block
                }
            }
        }
        rectF.set(
            0f + strokeWidth / 2,
            0f + strokeWidth / 2,
            width.toFloat() - strokeWidth / 2,
            height.toFloat() - strokeWidth / 2
        )
        when (shapeType) {
            1 -> canvas.drawRect(rectF, mStrokePaint)
            2 -> canvas.drawOval(rectF, mStrokePaint)
            else -> { // Note the block
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

}