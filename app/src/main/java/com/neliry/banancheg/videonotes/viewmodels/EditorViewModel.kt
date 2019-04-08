package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.text.SpannableStringBuilder
import android.util.Base64
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.neliry.banancheg.videonotes.entities.*
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.models.PageItem
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageItemsRepository
import kotlinx.android.synthetic.main.editor_activity.view.*
import kotlinx.android.synthetic.main.fragment_shape_editor.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

import android.util.DisplayMetrics



class EditorViewModel(application: Application) :BaseNavigationDrawerViewModel(application) {

    private var shapeView: ShapeView? = null
    internal val textBlockController = TextBlockController(this)
    internal val imageBlockController = ImageBlockController(this)
    private val textBlock = TextBlock(textBlockController)
    private val imageBox = ImageBlock(imageBlockController)
    var maxHeight = dpToPx(16f, getApplication()).toFloat()
    var pointX = 0f
    var pointY = 0f
    var pageId: String =""
    var isLoaded = false
    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageItemsRepository() as FirebaseDatabaseRepository<BaseItem>
    }

    internal fun removeFocus() {
        textBlockController.removeFocus(getApplication())
        imageBlockController.removeImageFocus()
    }

    internal fun createTextBlockController() {
        textBlockController.createController(getApplication())
        imageBlockController.createController(getApplication())
    }

    internal fun createTextBlock(layerWidth: Int): EditText {
        disableDraw()
        imageBlockController.removeImageFocus()
        return textBlock.createTextBlock(getApplication(), checkMaxHeight(), layerWidth)
    }

    internal fun createImageBlock(imageLayout: RelativeLayout, resultCode: Int, data: Intent?) {
        disableDraw()
        textBlockController.removeFocus(getApplication())
        val imageView: ImageView? = imageBox.setImage( resultCode, data, getApplication(), imageBox.createImageBlock(getApplication(), checkMaxHeight(),imageLayout.width))
        if(imageView != null)
            imageLayout.addView(imageView)
    }

    internal fun checkMaxHeight(): Float{

        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val imageLayer = canvasLayer.image_layer
        maxHeight = dpToPx(16f, getApplication()).toFloat()
        var childCount = textLayer.childCount
        for (i in 0 until childCount){
            val v = textLayer.getChildAt(i)
            if(maxHeight < v.y+v.height)
                maxHeight = v.y+v.height
        }

        childCount = imageLayer.childCount
        for (i in 0 until childCount){
            val v = imageLayer.getChildAt(i)
            if(maxHeight < v.y+v.height)
                maxHeight = v.y+v.height
        }

        val canvas = textLayer.parent as RelativeLayout
        canvas.layoutParams.height = (maxHeight + dpToPx(1000f, getApplication())).toInt()
        canvas.draw_event_layer.layoutParams.height =  canvas.layoutParams.height
        return maxHeight
    }

    internal fun setDrawLayerHeight(){
        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val canvas = textLayer.parent as RelativeLayout
        canvas.paint_layer.layoutParams.height = (maxHeight + dpToPx(1000f, getApplication())).toInt()
        canvas.paint_layer.requestLayout()
    }

    internal fun startDraw(fragment: Fragment, relativeLayout: RelativeLayout){
        if(fragment.shape_select.visibility == View.VISIBLE){
            fragment.shape_select.visibility = View.GONE
            fragment.advanced_shape_editor.visibility = View.GONE
            fragment.disable_draw_btn.callOnClick()
        }
        else{
            removeFocus()
            fragment.shape_select.visibility = View.VISIBLE
            createShape(fragment, relativeLayout)
        }
    }

    fun createShape(fragment: Fragment,  relativeLayout: RelativeLayout){
        val shapeView = ShapeView(getApplication(), fragment.shape_preview.attributeSet)
        val params = RelativeLayout.LayoutParams(
            0,
            0
        )
        shapeView!!.layoutParams = params
        shapeView!!.setOnClickListener {
            setShapeFocus(getApplication(), shapeView!!)
        }
        shapeView!!.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            view.post {
                changeControllerPosition(getApplication(), view)
            }
        }
        relativeLayout.addView(shapeView)
        shapeView!!.postInvalidate()
        this.shapeView = shapeView
    }

    private fun setShapeFocus(context: Context, shapeView: ShapeView) {
        disableDraw()
//        shapeView.bringToFront()
        imageBlockController.removeImageFocus()
        changeControllerPosition(context, shapeView)
        imageBlockController.setBlock(shapeView)
        imageBlockController.controllerLayout.visibility = View.VISIBLE
    }
    private fun changeControllerPosition(context: Context, imageView: View){
        val params: RelativeLayout.LayoutParams =
            imageBlockController.controllerLayout.layoutParams as RelativeLayout.LayoutParams
        params.width = imageView.width+dpToPx(30f, context)
        params.height = imageView.height+dpToPx(30f, context)
        params.setMargins(imageView.x.toInt()- dpToPx(15f, context), imageView.y.toInt()- dpToPx(15f, context), 0, 0)
        imageBlockController.controllerLayout.requestLayout()
    }

    fun drawEvent(event: MotionEvent, canvasLayer: RelativeLayout, fragment: Fragment): Boolean{
        return if(fragment.shape_preview.shapeType == 1 || fragment.shape_preview.shapeType == 2){
            createShapeEvent(event, canvasLayer, fragment)
        } else if(fragment.shape_preview.shapeType == 3 || fragment.shape_preview.shapeType == 4){
            canvasLayer.paint_layer.onTouchEvent(event)
        } else {
            false
        }
    }

    fun createShapeEvent(event: MotionEvent, v: RelativeLayout, fragment: Fragment): Boolean{
        if(shapeView!=null) {
            val p = shapeView!!.layoutParams as RelativeLayout.LayoutParams
            shapeView?.layoutParams = p
            // Checks for the event that occurs
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scrollView = v.parent as ScrollView
                    scrollView.requestDisallowInterceptTouchEvent(true)
                    shapeView!!.fillColor = fragment.shape_preview.fillColor
                    shapeView!!.strokeColor = fragment.shape_preview.strokeColor
                    shapeView!!.strokeWidth = fragment.shape_preview.strokeWidth
                    shapeView!!.shapeType = fragment.shape_preview.shapeType
                    shapeView!!.isFillColor = fragment.shape_preview.isFillColor
                    pointX = event.x
                    pointY = event.y
                    p.leftMargin = event.x.toInt()
                    p.topMargin = event.y.toInt()

                    p.width = 0
                    p.height = 0
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    var width = Math.round(event.x - pointX).toInt()
                    var height = (event.y - pointY).toInt()

                    if (width == 0)
                    {
                        width = 1
                    }

                    if (height == 0)
                    {
                        height = 1
                    }

                    if (width < 0)
                        p.leftMargin = pointX.toInt() - Math.abs(width)
                    else p.leftMargin = pointX.toInt()

                    if (height < 0)
                        p.topMargin = pointY.toInt() - Math.abs(height)
                    else {
                        p.topMargin = pointY.toInt()
                    }

                    p.height = Math.abs(height).toInt()
                    p.width = Math.abs(width).toInt()
                }
                MotionEvent.ACTION_UP -> {
                    val scrollView = v.parent as ScrollView
                    scrollView.requestDisallowInterceptTouchEvent(false)
                    createShape(fragment, v.image_layer)
                }
                else -> return false
            }
            // Force a view to draw again
            shapeView!!.postInvalidate()
            return true
        }
        else return false
    }

    fun disableDraw(){
        if(shapeView != null)
            if(shapeView!!.width == 0 || shapeView!!.height == 0)
                (shapeView!!.parent as ViewManager).removeView(shapeView)
        shapeView = null
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)

    fun saveData() {

        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val imageLayer = canvasLayer.image_layer
        val paintLayer = canvasLayer.paint_layer
        var myRef = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("views").child(pageId)
        myRef.removeValue()

        var textChildCount = textLayer.childCount
        for (i in 0 until textChildCount){
            val v = textLayer.getChildAt(i) as EditText
            saveTextAsync(v, myRef)
        }

        var imageChildCount = imageLayer.childCount
        for (i in 0 until imageChildCount){
            val v = imageLayer.getChildAt(i)
            if(v is ImageView){
                saveImageAsync(v, myRef)
            }
            if(v is ShapeView){
                saveShapeAsync(v, myRef)
            }
        }

        savePaintAsync(paintLayer, myRef)
    }

    private fun saveTextAsync(v: TextView, myRef: DatabaseReference) { // this: CoroutineScope
        if(v.text.toString() != ""){
            var item = PageItem("", v.text.toString(), "EditText", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            myRef.push().setValue(item)
        }
    }

    private fun saveImageAsync(v: ImageView, myRef: DatabaseReference) { // this: CoroutineScope
        val bitmap = (v.drawable as BitmapDrawable).bitmap
        var item = PageItem("", bitMapToString(bitmap), "ImageView", pxToDp(v.width.toFloat(), getApplication()),
            pxToDp(v.height.toFloat(), getApplication()),
            pxToDp(v.x, getApplication()),
            pxToDp(v.y, getApplication()))
        myRef.push().setValue(item)
    }

    private fun saveShapeAsync(v: ShapeView, myRef: DatabaseReference){ // this: CoroutineScope
        if(v.width!=0 && v.height!=0){
            val content: String = "shapeType:"+v.shapeType+";isFillColor:"+v.isFillColor+";strokeWidth:"+v.strokeWidth+";fillColor:"+v.fillColor+";strokeColor:"+v.strokeColor
            var item = PageItem("", content, "ShapeView", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            myRef.push().setValue(item)
        }
    }
    private fun savePaintAsync(paintLayer: SimpleDrawingView, myRef: DatabaseReference){ // this: CoroutineScope
        val bitmap = paintLayer.mBitmap
        var item = PageItem("", bitMapToString(bitmap), "PaintView", paintLayer.width.toFloat(), paintLayer.height.toFloat(), 0f, 0f)
        myRef.push().setValue(item)
    }

    fun loadPage(textLayout: RelativeLayout, imageLayout: RelativeLayout, simpleDrawingView: SimpleDrawingView, fragment: Fragment, notes: List<BaseItem>, progressBar: ProgressBar){

        if (notes != null && !isLoaded){
            val items = notes as List<PageItem>
            for(i in items){
                when (i.type){
                    "EditText" -> {
                        loadEditText(textLayout, i)
                    }
                    "ImageView" -> {
                        loadImageView(imageLayout, i)
                    }
                    "ShapeView" -> {
                        loadShapeView(imageLayout, fragment, i)
                    }
                    "PaintView" -> {
                        loadDrawingView(simpleDrawingView, i)
                    }
                }
            }
            textBlockController.removeFocus(getApplication())
            isLoaded = true
            fragment.shape_preview.shapeType = 0
            checkMaxHeight()
            progressBar.visibility = View.GONE
        }
    }

    private fun loadEditText(textLayout: RelativeLayout, item: PageItem){
        textLayout.addView(textBlock.loadTextBlock(getApplication(), SpannableStringBuilder(item.content), dpToPx(item.width!!, getApplication()) , dpToPx(item.height!!, getApplication()) , dpToPx(item.x!!, getApplication()) , dpToPx(item.y!!, getApplication()) ))
    }

    private fun loadImageView(imageLayout: RelativeLayout, item: PageItem){
        imageLayout.addView(imageBox.loadImageBlock(getApplication(), stringToBitMap(item.content!!)!!, dpToPx(item.width!!, getApplication()) , dpToPx(item.height!!, getApplication()) , dpToPx(item.x!!, getApplication()) , dpToPx(item.y!!, getApplication()) ))
    }

    private fun loadShapeView(imageLayout: RelativeLayout, fragment: Fragment, item: PageItem){
        val separated = item.content!!.split(";")
        fragment.shape_preview.shapeType = separated[0].split(":")[1].toInt()
        fragment.shape_preview.isFillColor = separated[1].split(":")[1].toBoolean()
        fragment.shape_preview.strokeWidth = separated[2].split(":")[1].toFloat()
        fragment.shape_preview.fillColor = separated[3].split(":")[1].toInt()
        fragment.shape_preview.strokeColor = separated[4].split(":")[1].toInt()
        createShape(fragment, imageLayout)
        shapeView!!.shapeType = fragment.shape_preview.shapeType
        shapeView!!.isFillColor = fragment.shape_preview.isFillColor
        shapeView!!.strokeColor = fragment.shape_preview.strokeColor
        shapeView!!.fillColor = fragment.shape_preview.fillColor
        shapeView!!.strokeWidth = fragment.shape_preview.strokeWidth
        val p = shapeView!!.layoutParams as RelativeLayout.LayoutParams
        p.width = dpToPx(item.width!!, getApplication())
        p.height = dpToPx(item.height!!, getApplication())
        p.leftMargin = dpToPx(item.x!!, getApplication())
        p.topMargin = dpToPx(item.y!!, getApplication())
        shapeView?.layoutParams = p
    }

    private fun loadDrawingView(simpleDrawingView: SimpleDrawingView, item: PageItem){
        simpleDrawingView.layoutParams.width = item.width!!.toInt()
        simpleDrawingView.layoutParams.height = item.height!!.toInt()
        simpleDrawingView.postInvalidate()
        simpleDrawingView.loadBitmap(stringToBitMap(item.content!!)!!, item.width!!.toInt(), item.height!!.toInt())
    }

    fun bitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun stringToBitMap(encodedString: String): Bitmap? {
        try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            return null
        }

    }

    fun parseIntent(intent: Intent, supportActionBar: androidx.appcompat.app.ActionBar){
        if (intent.getSerializableExtra("currentPage") !=null) {
            val page: Page = intent.getSerializableExtra("currentPage") as Page
            repository.setDatabaseReference("views", page.id.toString())
            supportActionBar.title = page.name
            pageId = page.id.toString()
        }
    }

    inner class YourAsyncTask (private val progressBar: ProgressBar): AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            disableDraw()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg args: Void?): Void? {
            saveData()
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE
        }

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    fun pxToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}