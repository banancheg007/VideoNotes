package com.neliry.banancheg.videonotes.entities

import android.app.Application
import android.content.Context
import android.util.TypedValue
import android.view.*
import android.widget.*
import android.view.MotionEvent
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel
import kotlinx.android.synthetic.main.image_block_controller_layout.view.*

class ImageBlockController(internal val model: EditorViewModel){

    lateinit var viewBlock: View
    lateinit var controllerLayout: RelativeLayout

    var x: Float = 0f
    var y: Float = 0f

    var touchedX: Float = 0f
    var touchedY: Float = 0f

    var height = 0f

    fun createController(context: Context): RelativeLayout
    {
        val inflater = LayoutInflater.from(context)
        controllerLayout = inflater.inflate(R.layout.image_block_controller_layout, null, false) as RelativeLayout

        controllerLayout.image_move_button.setOnTouchListener { view, motionEvent ->
                val scrollView = viewBlock.parent.parent.parent as ScrollView
                moveImageBlock(viewBlock, motionEvent, context, scrollView.scrollY)
            false
        }

        controllerLayout.image_resize_button.setOnTouchListener { view, motionEvent ->
            resizeImageBlock(viewBlock, motionEvent, context)
            false
        }

        controllerLayout.left_resize_button.setOnTouchListener { view, motionEvent ->
            leftResizeImageBlock(viewBlock, motionEvent, context)
            false
        }

        controllerLayout.right_resize_button.setOnTouchListener { view, motionEvent ->
            rightResizeImageBlock(viewBlock, motionEvent, context)
            false
        }

        controllerLayout.top_resize_button.setOnTouchListener { view, motionEvent ->
            topResizeImageBlock(viewBlock, motionEvent, context)
            false
        }

        controllerLayout.bottom_resize_button.setOnTouchListener { view, motionEvent ->
            bottomResizeImageBlock(viewBlock, motionEvent, context)
            false
        }

        controllerLayout.image_confirm_button.setOnClickListener {
            removeImageFocus()
        }

        controllerLayout.image_remove_button.setOnClickListener {
            if(viewBlock is ImageView)
                model.addUrlForDelete(viewBlock.transitionName)
            removeImageFocus()
            (viewBlock.parent as ViewManager).removeView(viewBlock)
        }

        return controllerLayout
    }



    fun removeImageFocus(){
        if(model.textBlockController.controllerLayout.visibility == View.VISIBLE)
            model.textBlockController.removeFocus(model.getApplication() as Application)
        if(controllerLayout.visibility == View.VISIBLE) {
            controllerLayout.visibility = View.GONE
        }
    }

    fun setBlock(setView : View){
        viewBlock = setView
    }

    private fun resizeImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(imageBlock is ImageView)
                    imageBlock.scaleType = ImageView.ScaleType.FIT_XY
                imageBlock.parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                x = imageBlock.x
                if (motionEvent.rawX < imageBlock.width + imageBlock.x) {
                    touchedX = imageBlock.width + imageBlock.x - motionEvent.rawX
                    x = imageBlock.x - touchedX
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val oldWidth = imageBlock.width
                var height = 0f
                if(Math.round(motionEvent.rawX - x) > dpToPx(45f, context) ){
                    imageBlock.layoutParams.width = Math.round(motionEvent.rawX - x)
//                    imageBlock.layoutParams.height = imageBlock.height - (oldWidth - Math.round(motionEvent.rawX - x))
                    controllerLayout.layoutParams.width = Math.round(motionEvent.rawX - x)+dpToPx(30f, context)
                    height = imageBlock.height / (oldWidth /(motionEvent.rawX - x))
                    imageBlock.layoutParams.height = height.toInt()
                    controllerLayout.layoutParams.height = height.toInt() + dpToPx(30f, context)
                }
                else{
                    controllerLayout.layoutParams.width = dpToPx(75f, context)
                    imageBlock.layoutParams.width = dpToPx(45f, context)
                    height = dpToPx(40f, context).toFloat()
                    imageBlock.layoutParams.height = height.toInt()
                }
                imageBlock.requestLayout()
            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {
                return
            }
        }
    }

    private fun leftResizeImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(imageBlock is ImageView)
                    imageBlock.scaleType = ImageView.ScaleType.FIT_XY
                imageBlock.parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                x = imageBlock.x + imageBlock.width
            }
            MotionEvent.ACTION_MOVE -> {
                val width = x - motionEvent.rawX
                val imageParams = imageBlock.layoutParams as RelativeLayout.LayoutParams
                val controllerParams = controllerLayout.layoutParams as RelativeLayout.LayoutParams

                if(width > dpToPx(45f, context)){
                    imageParams.leftMargin = motionEvent.rawX.toInt()
                    imageParams.width = width.toInt()
                    imageParams.height = imageBlock.height

                    controllerParams.leftMargin = motionEvent.rawX.toInt() - dpToPx(15f, context)
                    controllerParams.width = width.toInt()+dpToPx(30f, context)
                }
                else {
                    imageParams.leftMargin = x.toInt() - dpToPx(45f, context)
                    imageParams.width = dpToPx(45f, context)
                    imageParams.height = imageBlock.height

                    controllerParams.leftMargin = x.toInt() - dpToPx(60f, context)
                    controllerParams.width = dpToPx(75f, context)
                }


                imageBlock.layoutParams = imageParams
                controllerLayout.layoutParams = controllerParams
            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {
                return
            }
        }
    }

    private fun rightResizeImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(imageBlock is ImageView)
                    imageBlock.scaleType = ImageView.ScaleType.FIT_XY
                imageBlock.parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                x = imageBlock.x
                if (motionEvent.rawX < imageBlock.width + imageBlock.x) {
                    touchedX = imageBlock.width + imageBlock.x - motionEvent.rawX
                    x = imageBlock.x - touchedX
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if(Math.round(motionEvent.rawX - x) >= dpToPx(45f, context) ){
                    controllerLayout.layoutParams.width =  Math.round(motionEvent.rawX - x)+dpToPx(30f, context)
                    imageBlock.layoutParams.width = Math.round(motionEvent.rawX - x)
                    imageBlock.layoutParams.height = imageBlock.height
                }
                else{
                    controllerLayout.layoutParams.width = dpToPx(75f, context)
                    imageBlock.layoutParams.width = dpToPx(45f, context)
                }
                imageBlock.requestLayout()
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
                return
            }
        }
    }

    private fun topResizeImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(imageBlock is ImageView)
                    imageBlock.scaleType = ImageView.ScaleType.FIT_XY
                imageBlock.parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                height = imageBlock.y + imageBlock.height
                y = imageBlock.y
                touchedY = motionEvent.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val newHeight = height - ((y + (motionEvent.rawY - touchedY)).toInt())
                val imageParams = imageBlock.layoutParams as RelativeLayout.LayoutParams
                val controllerParams = controllerLayout.layoutParams as RelativeLayout.LayoutParams

                if(newHeight > dpToPx(45f, context)){
                    imageParams.topMargin = ((y + (motionEvent.rawY - touchedY)).toInt())
                    imageParams.height = newHeight.toInt()
                    imageParams.width = imageBlock.width

                    controllerParams.topMargin = ((y + (motionEvent.rawY - touchedY)).toInt()) - dpToPx(15f, context)
                    controllerParams.height = newHeight.toInt()+dpToPx(30f, context)
                }
                else {
                    imageParams.topMargin = height.toInt() - dpToPx(45f, context)
                    imageParams.height = dpToPx(45f, context)
                    imageParams.width = imageBlock.width

                    controllerParams.topMargin = height.toInt() - dpToPx(60f, context)
                    controllerParams.height = dpToPx(75f, context)
                }


                imageBlock.layoutParams = imageParams
                controllerLayout.layoutParams = controllerParams
            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {
                return
            }
        }
    }

    private fun bottomResizeImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(imageBlock is ImageView)
                    imageBlock.scaleType = ImageView.ScaleType.FIT_XY
                imageBlock.parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                y = imageBlock.y
                touchedY = motionEvent.rawY

                height = imageBlock.height.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                val newHeight = ((motionEvent.rawY - touchedY + height)).toInt()
                if(newHeight >= dpToPx(45f, context) ){
                    imageBlock.layoutParams.width = imageBlock.width
                    imageBlock.layoutParams.height = newHeight
                    controllerLayout.layoutParams.height = newHeight + dpToPx(30f, context)
                }
                else{
                    controllerLayout.layoutParams.height = dpToPx(75f, context)
                    imageBlock.layoutParams.height = dpToPx(45f, context)
                }
                imageBlock.requestLayout()
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
                return
            }
        }
    }

    private fun moveImageBlock(imageBlock: View, motionEvent: MotionEvent?, context: Context, scroll: Int) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val scrollView = imageBlock.parent.parent.parent as ScrollView
                scrollView.requestDisallowInterceptTouchEvent(true)

                x = imageBlock.x
                y = imageBlock.y
                touchedX = motionEvent.rawX
                touchedY = motionEvent.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val textBlockParams = imageBlock.layoutParams as RelativeLayout.LayoutParams
                val controllerLayoutParams = controllerLayout.layoutParams as RelativeLayout.LayoutParams
                textBlockParams.leftMargin = (x + (motionEvent.rawX - touchedX)).toInt()
                textBlockParams.topMargin = (y + (motionEvent.rawY - touchedY)).toInt()
                controllerLayoutParams.leftMargin = (x + (motionEvent.rawX - touchedX)).toInt()- dpToPx(15f, context)
                controllerLayoutParams.topMargin = (y + (motionEvent.rawY - touchedY)).toInt() - dpToPx(15f, context)

                imageBlock.layoutParams = textBlockParams
                controllerLayout.layoutParams = controllerLayoutParams
            }
            MotionEvent.ACTION_UP -> {
                imageBlock.layoutParams.width = imageBlock.width
            }
            else -> {
                return
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}