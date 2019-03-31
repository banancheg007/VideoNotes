package com.example.ghproject.activity.entities

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SimpleDrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var shapeType = 1
    private val mPath: Path = Path()
    var isEraseMode = false
    var pathList: MutableList<Path> = mutableListOf()
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private var mX: Float = 0f
    private var mY: Float = 0f
    internal var mPaint: Paint = Paint()
    private val mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)

    var myMatrix: Matrix

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setupPaint()
        myMatrix = Matrix()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if( ::mBitmap.isInitialized){
            changeMaxHeight(h)
        }
        else{
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBitmap!!)
//            val newBitmap = Bitmap.createBitmap(mBitmap.width, h, Bitmap.Config.ARGB_8888)
//            mCanvas = Canvas(newBitmap)
////            canvas!!.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
//            mBitmap = newBitmap
//            mCanvas = Canvas(newBitmap!!)
//            mCanvas!!.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
//            mBitmap = newBitmap
        }
    }

    fun changeMaxHeight(h: Int)= GlobalScope.async { // this: CoroutineScope
        launch {

            val newBitmap = Bitmap.createBitmap(mBitmap.width, h, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas (newBitmap)
            mCanvas!!.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
            mBitmap = newBitmap
        }
    }

    private fun setupPaint() {
        // Setup paint with color and stroke styles
        mPaint = Paint()
        mPaint!!.color = Color.BLACK
        mPaint!!.isAntiAlias = true
        mPaint!!.strokeWidth = 15f
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPaint!!.strokeCap = Paint.Cap.ROUND
//        drawPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.drawPath(mPath, mPaint!!)
    }



    private fun touch_start(x: Float, y: Float) {
        if(shapeType == 4){
            mPaint!!.xfermode = PorterDuffXfermode(
                PorterDuff.Mode.CLEAR)
        }
        else
            mPaint!!.xfermode = null
        val scrollView = this.parent.parent as ScrollView
        scrollView.requestDisallowInterceptTouchEvent(true)
        mPath.reset()
        mPath.moveTo(x, y)
        pathList.add(mPath)
        mX = x
        mY = y

    }

    private fun touch_move(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
        if (shapeType == 4){
            mPath.lineTo(x, y)
            mCanvas!!.drawPath(mPath, mPaint!!)
            mPath.reset()
            mPath.moveTo(x, y)
        }
    }

    private fun touch_up() {
        val scrollView = this.parent.parent as ScrollView
        scrollView.requestDisallowInterceptTouchEvent(false)
        mPath.lineTo(mX, mY)
        // commit the path to our offscreen
        mCanvas!!.drawPath(mPath, mPaint!!)
        // kill this so we don't double draw
        mPath.reset()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(shapeType !in 3..4)
            return false

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
    }

    companion object {
        private val TOUCH_TOLERANCE = 4f
    }

}