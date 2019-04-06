package com.neliry.banancheg.videonotes.entities

import android.content.Context
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.neliry.banancheg.videonotes.fragments.ShapeEditorFragment
import kotlinx.android.synthetic.main.editor_activity.view.*

class TextBlock(private val blockController: TextBlockController) {

    fun createTextBlock(context: Context, maxHeight: Float, layerWidth: Int): EditText {
        val editText = EditText(context)
        val params = RelativeLayout.LayoutParams(
            layerWidth-dpToPx(30f, context),
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            dpToPx(24f, context),
            maxHeight.toInt(),
            0,
            0
        )
        editText.layoutParams = params
        editText.background = null

        editText.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            editText.post {
                if (blockController.viewBlock == editText) {
                    changeControllerPosition(context, view as EditText)
                }
            }
        }

        editText.setOnClickListener {

            if (blockController.viewBlock != it || blockController.controllerLayout.visibility == View.GONE) {
                it.isFocusable = true
                it.isFocusableInTouchMode = true
                it.requestFocus()
//                isOnFocus = true
                setFocus(context, it as EditText)
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }

        setFocus(context, editText)
        editText.requestFocus()

        return editText
    }

    fun loadTextBlock(context: Context, content: Editable, width: Int, height: Int, x: Int, y: Int): EditText {
        val editText = EditText(context)
        val params = RelativeLayout.LayoutParams(
            width,
            height
        )
        params.setMargins(
            x,
            y,
            0,
            0
        )
        editText.layoutParams = params
        editText.background = null

        editText.text = content

        setFocus(context, editText)
        editText.requestFocus()

        editText.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            editText.post {
                if (blockController.viewBlock == editText) {
                    changeControllerPosition(context, view as EditText)
                }
            }
        }

        editText.setOnClickListener {

            if (blockController.viewBlock != it || blockController.controllerLayout.visibility == View.GONE) {
                it.isFocusable = true
                it.isFocusableInTouchMode = true
                it.requestFocus()
//                isOnFocus = true
                setFocus(context, it as EditText)
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }

        return editText
    }

    private fun setFocus(context: Context, editText:EditText) {
        blockController.model.disableDraw()
        blockController.removeFocus(context)
        changeControllerPosition(context, editText)
        blockController.setBlock(editText)
        blockController.controllerLayout.visibility = View.VISIBLE

    }

    private fun changeControllerPosition(context: Context, editText: EditText){
        val params: RelativeLayout.LayoutParams =
            blockController.controllerLayout.layoutParams as RelativeLayout.LayoutParams
        params.width = editText.width+dpToPx(44f, context)
        params.height = editText.height+dpToPx(30f, context)
        params.setMargins(editText.x.toInt() - dpToPx(22f, context), editText.y.toInt() - dpToPx(15f, context), 0, 0)
        blockController.controllerLayout.requestLayout()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}