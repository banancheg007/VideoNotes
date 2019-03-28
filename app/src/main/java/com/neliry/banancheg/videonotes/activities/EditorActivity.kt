package com.neliry.banancheg.videonotes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel
import com.neliry.banancheg.videonotes.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.editor_activity.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*

class EditorActivity : AppCompatActivity() {

    private val editorViewModel: EditorViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get(EditorViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( com.neliry.banancheg.videonotes.R.layout.editor_activity)

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
            fragment_shape_editor.shape_select.visibility = View.GONE
            fragment_shape_editor.advanced_shape_editor.visibility = View.GONE
            fragment_shape_editor.disable_draw_btn.callOnClick()
        }

        add_image_btn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            val RESULT_LOAD_IMG = 0
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
            fragment_shape_editor.shape_select.visibility = View.GONE
            fragment_shape_editor.advanced_shape_editor.visibility = View.GONE
            fragment_shape_editor.disable_draw_btn.callOnClick()
        }

        start_draw_btn.setOnClickListener {
            editorViewModel.startDraw(fragment_shape_editor, image_layer)
        }

//        val params = RelativeLayout.LayoutParams(
//            RelativeLayout.LayoutParams.MATCH_PARENT,
//            RelativeLayout.LayoutParams.MATCH_PARENT
//        )
//        draw_event_layer.layoutParams = params
        draw_event_layer.setOnTouchListener { v, event ->
            editorViewModel.drawEvent(event, canvas_layer, fragment_shape_editor)
        }

    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        editorViewModel.createImageBlock(image_layer, resultCode, data)
    }
}
