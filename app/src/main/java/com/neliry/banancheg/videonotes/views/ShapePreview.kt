package com.neliry.banancheg.videonotes.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class ShapePreview(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val attributeSet: AttributeSet = attrs
    private val mFillPaint: Paint = Paint()
    private val mStrokePaint: Paint = Paint()
    private val rectF: RectF = RectF()
    var isFillColor: Boolean = true

    var shapeType = 1

    var strokeWidth = 15f
    var fillColor = Color.parseColor("#646464")
    var strokeColor = Color.parseColor("#000000")

    override fun onDraw(canvas: Canvas) {
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.color = strokeColor
        mStrokePaint.strokeWidth = strokeWidth
        mStrokePaint.strokeCap = Paint.Cap.ROUND
        if(shapeType in 1..2){
            if(isFillColor) {
                mFillPaint.style = Paint.Style.FILL
                mFillPaint.color = fillColor

                rectF.set(
                    0f + dpToPx(35f, context),
                    0f + dpToPx(15f, context),
                    width.toFloat() - dpToPx(35f, context),
                    height.toFloat() - dpToPx(15f, context)
                )
                when (shapeType) {
                    1 -> canvas.drawRect(rectF, mFillPaint)
                    2 -> canvas.drawOval(rectF, mFillPaint)
                    else -> { // Note the block
                    }
                }
            }
            rectF.set(0f+dpToPx(35f, context)+strokeWidth/2, 0f+dpToPx(15f, context)+strokeWidth/2, width.toFloat()-dpToPx(35f, context)-strokeWidth/2, height.toFloat()-dpToPx(15f, context)-strokeWidth/2)
            when (shapeType) {
                1 ->  canvas.drawRect(rectF, mStrokePaint)
                2 ->  canvas.drawOval(rectF, mStrokePaint)
                else -> { // Note the block
                }
            }
        }
        else {
            if(shapeType == 4)
                mStrokePaint.color = Color.parseColor("#ffffff")
            val path = Path()
            path.moveTo(dpToPx(25f, context).toFloat(), height.toFloat()/2)
//            path.quadTo(200f, 300f, 600f, 100f)
            path.cubicTo(dpToPx(130f, context).toFloat(),height.toFloat()+dpToPx(10f, context).toFloat(), width.toFloat()-dpToPx(120f, context),-dpToPx(12f, context).toFloat(), width.toFloat()-dpToPx(25f, context), height.toFloat()/2)
            canvas.drawPath(path, mStrokePaint)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

}