package com.neliry.banancheg.videonotes.entities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException

class ImageBlock(private val blockController: ImageBlockController)  {


    fun createImageBlock(context: Context, maxHeight: Float, layerWidth: Int): ImageView {
        val imageView = ImageView(context)

        val params = RelativeLayout.LayoutParams(
            layerWidth-dpToPx(30f, context),
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            dpToPx(18f, context),
            maxHeight.toInt(),
            0,
            0
        )

        imageView.adjustViewBounds = true
        imageView.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            view.post {
                    changeControllerPosition(context, view as ImageView)
            }
        }

        imageView.setOnClickListener{
            setFocus(context, imageView)
        }

        imageView.layoutParams = params
        return imageView
    }

    fun setImage(resultCode: Int, data: Intent?, context: Context, imageView: ImageView): ImageView?{
        if (resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = context.contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                if (selectedImage.width > 1200 || selectedImage.height >1200){
                    val i: Float = Math.max(selectedImage.width, selectedImage.height).toFloat()
                    val indexer = 1200/i
                    val scaled = Bitmap.createScaledBitmap(selectedImage, (selectedImage.width*indexer).toInt(), (selectedImage.height*indexer).toInt(), true)
                    scaled.width
                    scaled.height
                    imageView.setImageBitmap(scaled)
                }
                else{
                    imageView.setImageBitmap(selectedImage)
                }
                imageView.transitionName = ""

                setFocus(context, imageView)
                return imageView
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                return null
            }
        }
        else {
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show()
            return null
        }
    }

    fun loadImageBlock(context: Context, content: String, width: Int, height: Int, x: Int, y: Int): ImageView {
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
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
        imageView.layoutParams = params
        imageView.background = null
        Picasso.with(context)
            .load(content)
            .fit()
            .centerCrop()
            .into(imageView)
        imageView.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            view.post {
                changeControllerPosition(context, view as ImageView)
            }
        }
        imageView.transitionName = content
        imageView.setOnClickListener{
            setFocus(context, imageView)
        }

        return imageView
    }

    private fun setFocus(context: Context, imageView: ImageView) {
        blockController.model.disableDraw()
        blockController.removeImageFocus()
        changeControllerPosition(context, imageView)
        blockController.setBlock(imageView)
        blockController.controllerLayout.visibility = View.VISIBLE
    }

    private fun changeControllerPosition(context: Context, imageView: ImageView){
        val params: RelativeLayout.LayoutParams =
            blockController.controllerLayout.layoutParams as RelativeLayout.LayoutParams
        params.width = imageView.width+dpToPx(30f, context)
        params.height = imageView.height+dpToPx(30f, context)
        params.setMargins(imageView.x.toInt()- dpToPx(15f, context), imageView.y.toInt()- dpToPx(15f, context), 0, 0)
        blockController.controllerLayout.requestLayout()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}