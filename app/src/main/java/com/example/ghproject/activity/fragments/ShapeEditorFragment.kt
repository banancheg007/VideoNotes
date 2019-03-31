package com.example.ghproject.activity.fragments

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import com.example.ghproject.R
import com.example.ghproject.activity.entities.SimpleDrawingView
import com.example.ghproject.activity.viewmodels.EditorViewModel
import kotlinx.android.synthetic.main.editor_activity.view.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*


class ShapeEditorFragment : androidx.fragment.app.Fragment() {

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
            changeBorderColor(border_color_1, paintLayer)
        }
        border_color_2.setOnClickListener {
            changeBorderColor(border_color_2, paintLayer)
        }
        border_color_3.setOnClickListener {
            changeBorderColor(border_color_3, paintLayer)
        }
        border_color_4.setOnClickListener {
            changeBorderColor(border_color_4, paintLayer)
        }
        border_color_5.setOnClickListener {
            changeBorderColor(border_color_5, paintLayer)
        }
        border_color_6.setOnClickListener {
            changeBorderColor(border_color_6, paintLayer)
        }
        border_color_7.setOnClickListener {
            changeBorderColor(border_color_7, paintLayer)
        }
        border_color_8.setOnClickListener {
            changeBorderColor(border_color_8, paintLayer)
        }
        border_color_9.setOnClickListener {
            changeBorderColor(border_color_9, paintLayer)
        }
        border_color_10.setOnClickListener {
            changeBorderColor(border_color_10, paintLayer)
        }
        background_color_1.setOnClickListener {
            changeBackgroundColor(background_color_1)
        }
        background_color_2.setOnClickListener {
            changeBackgroundColor(background_color_2)
        }
        background_color_3.setOnClickListener {
            changeBackgroundColor(background_color_3)
        }
        background_color_4.setOnClickListener {
            changeBackgroundColor(background_color_4)
        }
        background_color_5.setOnClickListener {
            changeBackgroundColor(background_color_5)
        }
        background_color_6.setOnClickListener {
            changeBackgroundColor(background_color_6)
        }
        background_color_7.setOnClickListener {
            changeBackgroundColor(background_color_7)
        }
        background_color_8.setOnClickListener {
            changeBackgroundColor(background_color_8)
        }
        background_color_9.setOnClickListener {
            changeBackgroundColor(background_color_9)
        }
        background_color_10.setOnClickListener {
            changeBackgroundColor(background_color_10)
        }

        draw_square_editor_btn.setOnClickListener {
            selectShapeButton(draw_square_editor_btn, shape_editor_layout)
            selectShapeButton(draw_square_btn, shape_select)
            shape_preview.shapeType = 1
            paintLayer.shapeType = 1
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer )
        }

        draw_circle_editor_btn.setOnClickListener {
            selectShapeButton(draw_circle_editor_btn, shape_editor_layout)
            selectShapeButton(draw_circle_btn, shape_select)
            shape_preview.shapeType = 2
            paintLayer.shapeType = 2
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_line_editor_btn.setOnClickListener {
            selectShapeButton(draw_line_editor_btn, shape_editor_layout)
            selectShapeButton(draw_line_btn, shape_select)
            shape_preview.shapeType = 3
            paintLayer.shapeType = 3
            shape_preview.postInvalidate()
        }
        eraser_editor_btn.setOnClickListener {
            selectShapeButton(eraser_editor_btn, shape_editor_layout)
            selectShapeButton(eraser_btn, shape_select)
            shape_preview.shapeType = 4
            paintLayer.shapeType = 4
            shape_preview.postInvalidate()
        }
        draw_square_btn.setOnClickListener {
            selectShapeButton(draw_square_btn, shape_select)
            selectShapeButton(draw_square_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 1
            paintLayer.shapeType = 1
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_circle_btn.setOnClickListener {
            selectShapeButton(draw_circle_btn, shape_select)
            selectShapeButton(draw_circle_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 2
            paintLayer.shapeType = 2
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
            editorViewModel.createShape(this, canvasLayer.editor_scroll_view.canvas_layer.image_layer)
        }
        draw_line_btn.setOnClickListener {
            selectShapeButton(draw_line_btn, shape_select)
            selectShapeButton(draw_line_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 3
            paintLayer.shapeType = 3
            shape_preview.postInvalidate()
            editorViewModel.setDrawLayerHeight()
        }

        eraser_btn.setOnClickListener {
            selectShapeButton(eraser_btn, shape_select)
            selectShapeButton(eraser_editor_btn, shape_editor_layout)
            shape_preview.shapeType = 4
            paintLayer.shapeType = 4
            editorViewModel.disableDraw()
            shape_preview.postInvalidate()
        }

        disable_draw_btn.setOnClickListener {
            selectShapeButton(disable_draw_btn, shape_select)
            selectShapeButton(null, shape_editor_layout)
            shape_preview.shapeType = 0
            paintLayer.shapeType = 0
            shape_preview.postInvalidate()
            editorViewModel.disableDraw()
        }
        color_fill_btn.setOnClickListener {
            if(shape_preview.isFillColor)
            {
                color_fill_btn.setBackgroundColor(Color.parseColor("#fafafa"))
                selectColorButton(null, shape_colors)
            }
            else{
                color_fill_btn.setBackgroundResource( R.drawable.border)
                selectColorButton(lastColorButton, shape_colors)
            }

            shape_preview.isFillColor =!shape_preview.isFillColor
            shape_preview.postInvalidate()
        }
    }

    private fun changeBorderColor(colorButton: ImageButton, paintLayer: SimpleDrawingView){
        val drawable = colorButton.drawable as ColorDrawable
        shape_preview.strokeColor = drawable.color
        paintLayer.mPaint.color = drawable.color
        shape_preview.postInvalidate()
        selectColorButton(colorButton, border_colors)
    }

    private fun changeBackgroundColor(colorButton: ImageButton){
        lastColorButton = colorButton
        if(!shape_preview.isFillColor)
        {
            color_fill_btn.setBackgroundResource( R.drawable.border)
            shape_preview.isFillColor =!shape_preview.isFillColor
        }
        val drawable = colorButton.drawable as ColorDrawable
        shape_preview.fillColor = drawable.color
        shape_preview.postInvalidate()
        selectColorButton(colorButton, shape_colors)
    }

    private fun selectColorButton(colorButton: ImageButton?, linearLayout: LinearLayout){
        var childCount = linearLayout.childCount
        for (i in 1 until childCount){
            val v = linearLayout.getChildAt(i)
            v.setPadding(dpToPx(0.5f, context!!), dpToPx(0.5f, context!!), dpToPx(0.5f, context!!), dpToPx(0.5f, context!!))
            v.setBackgroundColor(-0x5E5E5E)
        }
        if(colorButton != null) {
            colorButton!!.setBackgroundResource(R.drawable.btn_selected_border)
            colorButton!!.setPadding(
                dpToPx(3.5f, context!!),
                dpToPx(3.5f, context!!),
                dpToPx(3.5f, context!!),
                dpToPx(3.5f, context!!)
            )
        }
    }

    private fun selectShapeButton(shapeButton: ImageButton?, linearLayout: LinearLayout){
        var childCount = linearLayout.childCount
        for (i in 0 until childCount){
            val v = linearLayout.getChildAt(i)
            v.setBackgroundColor(Color.parseColor("#fafafa"))
        }
        shapeButton?.setBackgroundResource( R.drawable.btn_selected_border)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}
