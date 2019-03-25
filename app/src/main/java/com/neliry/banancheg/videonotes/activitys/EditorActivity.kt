package com.neliry.banancheg.videonotes

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.editor_activity.*

class EditorActivity : AppCompatActivity() {

    private val editorViewModel:  EditorViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get(EditorViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.neliry.banancheg.videonotes.R.layout.editor_activity)

        editorViewModel.createTextBlockController()
        controller_layer.addView(editorViewModel.textBlockController.controllerLayout)
        controller_layer.addView(editorViewModel.imageBlockController.controllerLayout)

        editor_scroll_view.viewTreeObserver.addOnScrollChangedListener {
            editorViewModel.checkMaxHeight()
        }

        canvas_layer.setOnClickListener{
            editorViewModel.removeFocus()
        }

        add_text_field_btn.setOnClickListener {
            text_layer.addView(editorViewModel.createTextBlock(text_layer.width))
            // show keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        add_image_btn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            val RESULT_LOAD_IMG = 0
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
        }

        start_draw_btn.setOnClickListener {
            editorViewModel.startDraw(fragment_shape_editor, draw_shape_layer)
        }

        canvas_layer.setOnTouchListener { v, event ->
            editorViewModel.createShapeEvent(event, v as RelativeLayout, fragment_shape_editor)
        }

    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        editorViewModel.createImageBlock(image_layer, resultCode, data)
    }
}
