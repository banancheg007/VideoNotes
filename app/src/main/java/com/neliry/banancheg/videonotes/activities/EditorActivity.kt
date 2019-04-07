package com.neliry.banancheg.videonotes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel
import com.neliry.banancheg.videonotes.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.editor_activity.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*

class EditorActivity : AppCompatActivity() {

    private val editorViewModel: EditorViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get(EditorViewModel::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( com.neliry.banancheg.videonotes.R.layout.editor_activity)

        val intent: Intent = intent
        editorViewModel.parseIntent(intent, supportActionBar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        editorViewModel.createTextBlockController()
        controller_layer.addView(editorViewModel.textBlockController.controllerLayout)
        controller_layer.addView(editorViewModel.imageBlockController.controllerLayout)

        editorViewModel.getItems().observe(this, Observer { notes ->
            editorViewModel.loadPage(text_layer, image_layer, paint_layer, fragment_shape_editor, notes, editor_loading_indicator)
        })

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

        draw_event_layer.setOnTouchListener { v, event ->
            editorViewModel.drawEvent(event, canvas_layer, fragment_shape_editor)
        }

    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        editorViewModel.createImageBlock(image_layer, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.editor_activity_manu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId === android.R.id.home) {
            finish()
        }
        if (item.itemId === R.id.action_save_page) {

            editorViewModel.YourAsyncTask(editor_loading_indicator).execute()
        }
        return super.onOptionsItemSelected(item)
    }
}
