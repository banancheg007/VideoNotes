package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import java.io.ByteArrayOutputStream

import android.util.DisplayMetrics
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


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
    var pageContent: String? = null
    lateinit var conspId: String
    lateinit var noteBitmap: Bitmap

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
        if(shapeView != null && isLoaded)
            if(shapeView!!.width == 0 || shapeView!!.height == 0)
                (shapeView!!.parent as ViewManager).removeView(shapeView)
        shapeView = null
    }

    fun saveData() {

        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val textLayer = canvasLayer.text_layer
        val imageLayer = canvasLayer.image_layer
        val paintLayer = canvasLayer.paint_layer
        var myRef = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("views").child(pageId)
        myRef.removeValue()

        var imageChildCount = imageLayer.childCount
        for (i in 0 until imageChildCount){
            val v = imageLayer.getChildAt(i)
            if(v is ImageView){
                saveImage(v, myRef)
            }
            if(v is ShapeView){
                saveShape(v, myRef)
            }
        }

        savePaint(paintLayer, myRef)

        var textChildCount = textLayer.childCount
        for (i in 0 until textChildCount){
            val v = textLayer.getChildAt(i) as EditText
            saveText(v, myRef)
        }

        val stream = ByteArrayOutputStream()
        noteBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        if(pageContent != null){
            val contentRef = FirebaseStorage.getInstance().getReferenceFromUrl(pageContent!!)
            contentRef.delete()
            pageContent = null
        }
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis().toString()+".png")
        storageReference.putBytes(byteArray).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                var myRef = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("pages").child(conspId).child(pageId)
                myRef.child("content").setValue(it.toString())
                myRef.child("height").setValue(paintLayer.height)
                myRef.child("width").setValue(paintLayer.width)
                pageContent = it.toString()
            }
        }
    }

    private fun saveText(v: TextView, myRef: DatabaseReference) { // this: CoroutineScope
        if(v.text.toString() != ""){
            var item = PageItem("", v.text.toString(), "EditText", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            myRef.push().setValue(item)

            val bitmap = loadBitmapFromView(v)
            val canvas = Canvas(noteBitmap)
            canvas.drawBitmap(bitmap,  v.x , v.y , null)
        }
    }

    private fun saveImage(v: ImageView, myRef: DatabaseReference) { // this: CoroutineScope
        val separated = v.transitionName!!.split(";")
        Log.i("myTag", "0: "+separated[0])
        Log.i("myTag", "1: "+separated[1])
        if(separated[1] != ""){
            var item = PageItem("", separated[1], "ImageView", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            myRef.push().setValue(item)
        }
        else {
            val myUri = Uri.parse(separated[0])
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis().toString() + "." + getFileExtention(myUri, getApplication()))
            storageReference.putFile(myUri).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    var item = PageItem("", it.toString(), "ImageView", pxToDp(v.width.toFloat(), getApplication()),
                        pxToDp(v.height.toFloat(), getApplication()),
                        pxToDp(v.x, getApplication()),
                        pxToDp(v.y, getApplication()))
                    myRef.push().setValue(item)
                    v.transitionName = v.transitionName+item.content
                    Log.i("myTag", "item.content: "+item.content)
                    Log.i("myTag", "item.content: "+v.transitionName)
                }

            }
        }

        val bitmap = (v.drawable as BitmapDrawable).bitmap
        val canvas = Canvas(noteBitmap)
        canvas.drawBitmap(bitmap,  v.x , v.y , null)

    }

    fun getFileExtention(uri: Uri, context: Context): String{
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun saveShape(v: ShapeView, myRef: DatabaseReference){ // this: CoroutineScope
        if(v.width!=0 && v.height!=0){
            val content: String = "shapeType:"+v.shapeType+";isFillColor:"+v.isFillColor+";strokeWidth:"+v.strokeWidth+";fillColor:"+v.fillColor+";strokeColor:"+v.strokeColor
            var item = PageItem("", content, "ShapeView", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            myRef.push().setValue(item)

            val bitmap = loadBitmapFromView(v)
            val canvas = Canvas(noteBitmap)
            canvas.drawBitmap(bitmap,  v.x , v.y , null)
        }
    }
    private fun savePaint(paintLayer: SimpleDrawingView, viewsRef: DatabaseReference){ // this: CoroutineScope
        val bitmap = paintLayer.mBitmap
        var item = PageItem("", bitMapToString(bitmap), "PaintView", paintLayer.width.toFloat(), paintLayer.height.toFloat(), 0f, 0f)
        viewsRef.push().setValue(item)

        val canvas = Canvas(noteBitmap)
        val mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
        canvas.drawBitmap(bitmap,  0f , 0f, mBitmapPaint)
    }

    fun loadBitmapFromView (v: View): Bitmap{
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(canvas)
        return bitmap
    }

    fun loadPage(textLayout: RelativeLayout, imageLayout: RelativeLayout, simpleDrawingView: SimpleDrawingView, fragment: Fragment, notes: List<BaseItem>, progressBar: ProgressBar, width: Int){

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
                        loadDrawingView(simpleDrawingView, i, width)
                    }
                }
            }
            textBlockController.removeFocus(getApplication())
            fragment.shape_preview.shapeType = 0
            checkMaxHeight()
            progressBar.visibility = View.GONE
            isLoaded = true
        }
    }

    private fun loadEditText(textLayout: RelativeLayout, item: PageItem){
        textLayout.addView(textBlock.loadTextBlock(getApplication(), SpannableStringBuilder(item.content), dpToPx(item.width!!, getApplication()) , dpToPx(item.height!!, getApplication()) , dpToPx(item.x!!, getApplication()) , dpToPx(item.y!!, getApplication()) ))
    }

    private fun loadImageView(imageLayout: RelativeLayout, item: PageItem){
        imageLayout.addView(imageBox.loadImageBlock(getApplication(), item.content!!, dpToPx(item.width!!, getApplication()) , dpToPx(item.height!!, getApplication()) , dpToPx(item.x!!, getApplication()) , dpToPx(item.y!!, getApplication()) ))
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

    private fun loadDrawingView(simpleDrawingView: SimpleDrawingView, item: PageItem, width: Int){
        simpleDrawingView.layoutParams.width = width
        simpleDrawingView.layoutParams.height = (item.height!!*(width/item.width!!)).toInt()
        simpleDrawingView.postInvalidate()
        simpleDrawingView.loadBitmap(stringToBitMap(item.content!!)!!, width, item.height!!.toInt())
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
            pageContent = page.content
        }
        if (intent.getSerializableExtra("conspId") !=null) {
            conspId = intent.getSerializableExtra("conspId") as String
        }
    }

    inner class YourAsyncTask (private val progressBar: ProgressBar,val paintLayer: SimpleDrawingView): AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            disableDraw()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg args: Void?): Void? {
            noteBitmap = Bitmap.createBitmap(paintLayer.mBitmap.width, paintLayer.mBitmap.height, Bitmap.Config.ARGB_8888)
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