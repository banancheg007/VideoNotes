package com.neliry.banancheg.videonotes.fragments

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.entities.SimpleDrawingView
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel
import kotlinx.android.synthetic.main.editor_activity.view.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*
import kotlinx.android.synthetic.main.fragment_shape_editor.view.*

class ShapeEditorFragment : Fragment() {

    private lateinit var lastColorButton: ImageButton
    private lateinit var editorViewModel: EditorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shape_editor, container, false)
    }

    override fun onStart() {
        super.onStart()
        lastColorButton = background_color_2

        editorViewModel = activity?.run {
            ViewModelProviders.of(this).get(EditorViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        open_shape_editor.setOnClickListener{
            advanced_shape_editor.visibility = View.VISIBLE
        }

        close_shape_editor.setOnClickListener {
            advanced_shape_editor.visibility = View.GONE
        }

        val canvasLayer = advanced_shape_editor.parent.parent as LinearLayout
        val paintLayer = canvasLayer.editor_scroll_view.canvas_layer.paint_layer

        border_width_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                shape_preview.strokeWidth = i.toFloat()
                paintLayer.mPaint.strokeWidth = i.toFloat()
                shape_preview.postInvalidate()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        border_color_1.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_1, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_2.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_2, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_3.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_3, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_4.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_4, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_5.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_5, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_6.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_6, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_7.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_7, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_8.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_8, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_9.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_9, paintLayer, shape_preview, border_colors, context!!)
        }
        border_color_10.setOnClickListener {
            editorViewModel.changeBorderColor(border_color_10, paintLayer, shape_preview, border_colors, context!!)
        }
        background_color_1.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_1, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_2.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_2, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_3.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_3, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_4.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_4, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_5.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_5, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_6.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_6, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_7.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_7, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_8.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_8, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_9.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_9, shape_preview, color_fill_btn, shape_colors, context!!)
        }
        background_color_10.setOnClickListener {
            lastColorButton = editorViewModel.changeBackgroundColor(background_color_10, shape_preview, color_fill_btn, shape_colors, context!!)
        }

        draw_square_editor_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_square_editor_btn, shape_editor_layout)
            editorViewModel.selectShapeButton(draw_square_btn, shape_select)
            shape_preview.shapeType = 1
            paintLayer.shapeType = 1
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer )
        }

        draw_circle_editor_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_circle_editor_btn, shape_editor_layout)
            editorViewModel.selectShapeButton(draw_circle_btn, shape_select)
            shape_preview.shapeType = 2
            paintLayer.shapeType = 2
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_line_editor_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_line_editor_btn, shape_editor_layout)
            editorViewModel.selectShapeButton(draw_line_btn, shape_select)
            shape_preview.shapeType = 3
            paintLayer.shapeType = 3
            shape_preview.postInvalidate()
        }
        eraser_editor_btn.setOnClickListener {
            editorViewModel.selectShapeButton(eraser_editor_btn, shape_editor_layout)
            editorViewModel.selectShapeButton(eraser_btn, shape_select)
            shape_preview.shapeType = 4
            paintLayer.shapeType = 4
            shape_preview.postInvalidate()
        }
        draw_square_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_square_btn, shape_select)
            editorViewModel.selectShapeButton(draw_square_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 1
            paintLayer.shapeType = 1
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_circle_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_circle_btn, shape_select)
            editorViewModel.selectShapeButton(draw_circle_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 2
            paintLayer.shapeType = 2
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_line_btn.setOnClickListener {
            editorViewModel.selectShapeButton(draw_line_btn, shape_select)
            editorViewModel.selectShapeButton(draw_line_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 3
            paintLayer.shapeType = 3
            shape_preview.postInvalidate()
            editorViewModel.setDrawLayerHeight()
        }

        eraser_btn.setOnClickListener {
            editorViewModel.selectShapeButton(eraser_btn, shape_select)
            editorViewModel.selectShapeButton(eraser_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 4
            paintLayer.shapeType = 4
            editorViewModel.disableDraw()
            shape_preview.postInvalidate()
        }

        disable_draw_btn.setOnClickListener {
            editorViewModel.selectShapeButton(disable_draw_btn, shape_select)
            editorViewModel.selectShapeButton(null, shape_editor_layout)
            shape_preview.shapeType = 0
            paintLayer.shapeType = 0
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
        }
        color_fill_btn.setOnClickListener {
            if(shape_preview.isFillColor)
            {
                color_fill_btn.setBackgroundColor(Color.parseColor("#fafafa"))
                editorViewModel.selectColorButton(null, shape_colors, context!!)
            }
            else{
                color_fill_btn.setBackgroundResource( R.drawable.border)
                editorViewModel.selectColorButton(lastColorButton, shape_colors, context!!)
            }

            shape_preview.isFillColor =!shape_preview.isFillColor
            shape_preview.postInvalidate()
        }
    }

}
