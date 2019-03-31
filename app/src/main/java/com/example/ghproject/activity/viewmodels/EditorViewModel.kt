package com.example.ghproject.activity.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import android.widget.*
import com.example.ghproject.activity.entities.*
import kotlinx.android.synthetic.main.editor_activity.view.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*

class EditorViewModel(application: Application) : BaseViewModel(application) {

    private var shapeView: ShapeView? = null
    internal val textBlockController = TextBlockController(this)
    internal val imageBlockController = ImageBlockController(this)
    private val textBlock = TextBlock(textBlockController)
    private val imageBox = ImageBlock(imageBlockController)
    var maxHeight = dpToPx(16f, getApplication()).toFloat()
    var pointX = 0f
    var pointY = 0f

    internal fun removeFocus() {
        textBlockController.removeFocus(getApplication())
        imageBlockController.removeImageFocus()
    }

    internal fun createTextBlockController() {
        textBlockController.createController(getApplication())
        imageBlockController.createController(getApplication())
    }

    internal fun createTextBlock(layerWidth: Int): EditText {
        disableDraw()
        imageBlockController.removeImageFocus()
        return textBlock.createTextBlock(getApplication(), checkMaxHeight(), layerWidth)
    }

    internal fun createImageBlock(imageLayout: RelativeLayout, resultCode: Int, data: Intent?) {
        disableDraw()
        textBlockController.removeFocus(getApplication())
        val imageView: ImageView? = imageBox.setImage( resultCode, data, getApplication(), imageBox.createImageBlock(getApplication(), checkMaxHeight(),imageLayout.width))
        if(imageView != null)
            imageLayout.addView(imageView)
    }

    internal fun checkMaxHeight(): Float{

        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val imageLayer = canvasLayer.image_layer
        maxHeight = dpToPx(16f, getApplication()).toFloat()
        var childCount = textLayer.childCount
        for (i in 0 until childCount){
            val v = textLayer.getChildAt(i)
            if(maxHeight < v.y+v.height)
                maxHeight = v.y+v.height
        }

        childCount = imageLayer.childCount
        for (i in 0 until childCount){
            val v = imageLayer.getChildAt(i)
            if(maxHeight < v.y+v.height)
                maxHeight = v.y+v.height
        }

        val canvas = textLayer.parent as RelativeLayout
        canvas.layoutParams.height = (maxHeight + dpToPx(1000f, getApplication())).toInt()
        canvas.draw_event_layer.layoutParams.height =  canvas.layoutParams.height
        return maxHeight
    }

    internal fun setDrawLayerHeight(){
        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val canvas = textLayer.parent as RelativeLayout
        canvas.paint_layer.layoutParams.height = (maxHeight + dpToPx(1000f, getApplication())).toInt()
        canvas.paint_layer.requestLayout()
    }

    internal fun startDraw(fragment: androidx.fragment.app.Fragment, relativeLayout: RelativeLayout){
        if(fragment.shape_select.visibility == View.VISIBLE){
            fragment.shape_select.visibility = View.GONE
            fragment.advanced_shape_editor.visibility = View.GONE
            fragment.disable_draw_btn.callOnClick()
        }
        else{
            removeFocus()
            fragment.shape_select.visibility = View.VISIBLE
            createShape(fragment, relativeLayout)
        }
    }

    fun createShape(fragment: androidx.fragment.app.Fragment, relativeLayout: RelativeLayout){
        val shapeView = ShapeView(getApplication(), fragment.shape_preview.attributeSet)
        val params = RelativeLayout.LayoutParams(
            0,
            0
        )
        shapeView!!.layoutParams = params
        shapeView!!.setOnClickListener {
            setShapeFocus(getApplication(), shapeView!!)
        }
        shapeView!!.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            view.post {
                changeControllerPosition(getApplication(), view)
            }
        }
        relativeLayout.addView(shapeView)
        shapeView!!.postInvalidate()
        this.shapeView = shapeView
        checkMaxHeight()
    }

    private fun setShapeFocus(context: Context, shapeView: ShapeView) {
        disableDraw()
//        shapeView.bringToFront()
        imageBlockController.removeImageFocus()
        changeControllerPosition(context, shapeView)
        imageBlockController.setBlock(shapeView)
        imageBlockController.controllerLayout.visibility = View.VISIBLE
    }
    private fun changeControllerPosition(context: Context, imageView: View){
        val params: RelativeLayout.LayoutParams =
            imageBlockController.controllerLayout.layoutParams as RelativeLayout.LayoutParams
        params.width = imageView.width+dpToPx(30f, context)
        params.height = imageView.height+dpToPx(30f, context)
        params.setMargins(imageView.x.toInt()- dpToPx(15f, context), imageView.y.toInt()- dpToPx(15f, context), 0, 0)
        imageBlockController.controllerLayout.requestLayout()
    }

    fun drawEvent(event: MotionEvent, canvasLayer: RelativeLayout, fragment: androidx.fragment.app.Fragment): Boolean{
        return if(fragment.shape_preview.shapeType == 1 || fragment.shape_preview.shapeType == 2){
            createShapeEvent(event, canvasLayer, fragment)
        } else if(fragment.shape_preview.shapeType == 3 || fragment.shape_preview.shapeType == 4){
//            canvasLayer.paint_layer.onTouchEvent(event)
            false
        } else {
            false
        }
    }

    fun createShapeEvent(event: MotionEvent, v: RelativeLayout, fragment: androidx.fragment.app.Fragment): Boolean{
        if(shapeView!=null) {
            val p = shapeView!!.layoutParams as RelativeLayout.LayoutParams

            shapeView?.layoutParams = p
            // Checks for the event that occurs
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scrollView = v.parent as ScrollView
                    scrollView.requestDisallowInterceptTouchEvent(true)
                    shapeView!!.fillColor = fragment.shape_preview.fillColor
                    shapeView!!.strokeColor = fragment.shape_preview.strokeColor
                    shapeView!!.strokeWidth = fragment.shape_preview.strokeWidth
                    shapeView!!.shapeType = fragment.shape_preview.shapeType
                    shapeView!!.isFillColor = fragment.shape_preview.isFillColor
                    pointX = event.x
                    pointY = event.y
                    p.leftMargin = event.x.toInt()
                    p.topMargin = event.y.toInt()

                    p.width = 0
                    p.height = 0
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    var width = Math.round(event.x - pointX).toInt()
                    var height = (event.y - pointY).toInt()

                    if (width == 0)
                    {
                        width = 1
                    }

                    if (height == 0)
                    {
                        height = 1
                    }

                    if (width < 0)
                        p.leftMargin = pointX.toInt() - Math.abs(width)
                    else p.leftMargin = pointX.toInt()

                    if (height < 0)
                        p.topMargin = pointY.toInt() - Math.abs(height)
                    else {
                        p.topMargin = pointY.toInt()
                    }

                    p.height = Math.abs(height).toInt()
                    p.width = Math.abs(width).toInt()
                }
                MotionEvent.ACTION_UP -> {
                    val scrollView = v.parent as ScrollView
                    scrollView.requestDisallowInterceptTouchEvent(false)
                    createShape(fragment, v.image_layer)
                }
                else -> return false
            }
            // Force a view to draw again
            shapeView!!.postInvalidate()
            return true
        }
        else return false
    }

    fun disableDraw(){
        if(shapeView != null)
            if(shapeView!!.width == 0 || shapeView!!.height == 0)
                (shapeView!!.parent as ViewManager).removeView(shapeView)
        shapeView = null
    }

//    fun selectColorButton(colorButton: ImageButton?, linearLayout: LinearLayout){
//        var childCount = linearLayout.childCount
//        for (i in 1 until childCount){
//            val v = linearLayout.getChildAt(i)
//            v.setPadding(dpToPx(0.5f, getApplication()!!), dpToPx(0.5f, getApplication()), dpToPx(0.5f, getApplication()), dpToPx(0.5f, getApplication()))
//            v.setBackgroundColor(-0x5E5E5E)
//        }
//        if(colorButton != null) {
//            colorButton!!.setBackgroundResource(R.drawable.btn_selected_border)
//            colorButton!!.setPadding(
//                dpToPx(3.5f, getApplication()),
//                dpToPx(3.5f, getApplication()),
//                dpToPx(3.5f, getApplication()),
//                dpToPx(3.5f, getApplication())
//            )
//        }
//    }
//
//    fun selectShapeButton(colorButton: ImageButton, linearLayout: LinearLayout){
//        var childCount = linearLayout.childCount
//        for (i in 0 until childCount){
//            val v = linearLayout.getChildAt(i)
//            v.setBackgroundColor(Color.parseColor("#fbfbfb"))
//        }
//        colorButton.setBackgroundResource( R.drawable.btn_selected_border)
//    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}