package com.neliry.banancheg.videonotes.entities

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.text_block_controller_layout.view.*
import android.view.MotionEvent
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel

class TextBlockController(internal val model: EditorViewModel){

    lateinit var viewBlock: EditText
    lateinit var controllerLayout: RelativeLayout

    var x: Float = 0f
    var y: Float = 0f

    var touchedX: Float = 0f
    var touchedY: Float = 0f

    fun createController(context: Context): RelativeLayout
    {
        val inflater = LayoutInflater.from(context)
        controllerLayout = inflater.inflate(R.layout.text_block_controller_layout, null, false) as RelativeLayout

        controllerLayout.move_button.setOnTouchListener { view, motionEvent ->
                val scrollView = viewBlock.parent.parent.parent as ScrollView
                moveTextBlock(viewBlock, motionEvent, context, scrollView.scrollY)
            false
        }

        controllerLayout.resize_button.setOnTouchListener { view, motionEvent ->
            val scrollView = viewBlock.parent.parent.parent as ScrollView
            resizeTextBlock(viewBlock, motionEvent, context, scrollView.scrollY)
            false
        }

        controllerLayout.confirm_button.setOnClickListener {
            removeFocus(context)
        }

        controllerLayout.remove_button.setOnClickListener {
            removeFocus(context)
            (viewBlock.parent as ViewManager).removeView(viewBlock)
        }

        return controllerLayout
    }



    fun removeFocus(context: Context){
        if(model.imageBlockController.controllerLayout.visibility == View.VISIBLE)
            model.imageBlockController.removeImageFocus()
        if(controllerLayout.visibility == View.VISIBLE) {
                hideKeyboard(context, viewBlock)
                viewBlock.clearFocus()
                viewBlock.isFocusable = false
                viewBlock.isFocusableInTouchMode = false
            controllerLayout.visibility = View.GONE
        }
    }

    fun setBlock(setView : EditText){
        viewBlock=setView
    }

    private fun resizeTextBlock(textBlock: View, motionEvent: MotionEvent?, context: Context, scroll: Int) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val scrollView = textBlock.parent.parent.parent as ScrollView
                scrollView.requestDisallowInterceptTouchEvent(true)
                hideKeyboard(context, textBlock)
                textBlock.clearFocus()
                viewBlock.isFocusable = false
                viewBlock.isFocusableInTouchMode = false

                scrollView.scrollY = scroll

                x = textBlock.x
                if (motionEvent.rawX < textBlock.width + textBlock.x) {
                    touchedX = textBlock.width + textBlock.x - motionEvent.rawX
                    x = textBlock.x - touchedX
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if(Math.round(motionEvent.rawX - x) >= dpToPx(20f, context) )
                    textBlock.layoutParams.width = Math.round(motionEvent.rawX - x)
                else
                    textBlock.layoutParams.width = dpToPx(20f, context)
               textBlock.requestLayout()
            }
            MotionEvent.ACTION_UP -> {
                viewBlock.isFocusable = true
                viewBlock.isFocusableInTouchMode = true
            }
            else -> {
                return
            }
        }
    }

    private fun moveTextBlock(textBlock: View, motionEvent: MotionEvent?, context: Context, scroll: Int) {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val scrollView = textBlock.parent.parent.parent as ScrollView
                scrollView.requestDisallowInterceptTouchEvent(true)
                hideKeyboard(context, textBlock)
                textBlock.clearFocus()
                viewBlock.isFocusable = false
                viewBlock.isFocusableInTouchMode = false

                scrollView.scrollY = scroll

                x = textBlock.x
                y = textBlock.y
                touchedX = motionEvent.rawX
                touchedY = motionEvent.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val textBlockParams = textBlock.layoutParams as RelativeLayout.LayoutParams
                val controllerLayoutParams = controllerLayout.layoutParams as RelativeLayout.LayoutParams
                textBlockParams.leftMargin = (x + (motionEvent.rawX - touchedX)).toInt()
                textBlockParams.topMargin = (y + (motionEvent.rawY - touchedY)).toInt()
                controllerLayoutParams.leftMargin = (x + (motionEvent.rawX - touchedX)).toInt()- dpToPx(22f, context)
                controllerLayoutParams.topMargin = (y + (motionEvent.rawY - touchedY)).toInt() - dpToPx(15f, context)

                textBlock.layoutParams = textBlockParams
                controllerLayout.layoutParams = controllerLayoutParams
            }
            MotionEvent.ACTION_UP -> {
                textBlock.layoutParams.width = textBlock.width

                viewBlock.isFocusable = true
                viewBlock.isFocusableInTouchMode = true
            }
            else -> {
                return
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}