package com.neliry.banancheg.videonotes.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.text.style.UnderlineSpan
import android.util.Log
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
import com.neliry.banancheg.videonotes.fragments.DeletePageDialogFragment
import com.neliry.banancheg.videonotes.fragments.RenamePageDialogFragment


class EditorActivity : AppCompatActivity() {



    private val editorViewModel: EditorViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get(EditorViewModel::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( com.neliry.banancheg.videonotes.R.layout.editor_activity)

        val intent: Intent = intent
        editorViewModel.parseIntent(intent, supportActionBar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        var width = size.x

        editorViewModel.createTextBlockController()
        controller_layer.addView(editorViewModel.textBlockController.controllerLayout)
        controller_layer.addView(editorViewModel.imageBlockController.controllerLayout)

        editorViewModel.getItems().observe(this, Observer { notes ->
            editorViewModel.loadPage(text_layer, image_layer, paint_layer, fragment_shape_editor, notes, editor_loading_indicator, width)
        })

        editor_scroll_view.viewTreeObserver.addOnScrollChangedListener {
            editorViewModel.checkMaxHeight()
        }

        canvas_layer.setOnClickListener{
            editorViewModel.removeFocus()
        }

        add_text_field_btn.setOnClickListener {
            editorViewModel.createTextBlock(text_layer.width, text_layer)
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

        start_voice_recogniser_btn.setOnClickListener {
            editorViewModel.startVoiceRecognicion( this@EditorActivity, voice_progress , text_layer)
        }

        bold_button.setOnClickListener {
            editorViewModel.setSpan("bold", this@EditorActivity)
        }

        italic_button.setOnClickListener {
            editorViewModel.setSpan("italic", this@EditorActivity)
        }

        underline_button.setOnClickListener {
            editorViewModel.setSpan("underline", this@EditorActivity)
        }

        strikethrough_button.setOnClickListener {
            editorViewModel.setSpan("strikethrough", this@EditorActivity)
        }

        text_color_button.setOnClickListener {
            editorViewModel.setSpan("text color", this@EditorActivity)
        }

        fill_color_button.setOnClickListener {
            editorViewModel.setSpan("fill color", this@EditorActivity)
        }

        align_left_button.setOnClickListener {
            editorViewModel.setSpan("align left", this@EditorActivity)
        }

        align_center_button.setOnClickListener {
            editorViewModel.setSpan("align center", this@EditorActivity)
        }

        align_right_button.setOnClickListener {
            editorViewModel.setSpan("align right", this@EditorActivity)
        }

        editorViewModel.getFinish().observe( this, Observer{
            if(it)
                finish()
        })
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
            editorViewModel.YourAsyncTask(editor_loading_indicator, paint_layer).execute()
        }

        if (item.itemId === R.id.action_delete_page){
            showDeleteDialog()
        }

        if (item.itemId === R.id.action_rename_page){
            showRenameDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        val fm = supportFragmentManager
        val deletePageDialogFragment = DeletePageDialogFragment()
        deletePageDialogFragment.show(fm, "deletePageDialogFragment")
    }

    private fun showRenameDialog() {
        val fm = supportFragmentManager
        val renamePageDialogFragment = RenamePageDialogFragment()
        renamePageDialogFragment.show(fm, "renamePageDialogFragment")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            editorViewModel.speechRecognizer!!.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:$e")
        }

    }

    companion object {
        private val TAG = "MyStt3Activity"
        private val RECORD_REQUEST_CODE = 101
    }
}
