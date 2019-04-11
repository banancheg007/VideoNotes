package com.neliry.banancheg.videonotes.viewmodels

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Html
import android.text.Spannable

import android.text.style.*
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
import android.view.Gravity
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.views.ShapePreview

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
    var isRecogniserActive = false
    var pageContent: String? = null
    lateinit var conspId: String
    lateinit var noteBitmap: Bitmap
    var urlList: ArrayList<String> = arrayListOf<String>()
    val speechRecognizer: SpeechRecognizer? = SpeechRecognizer.createSpeechRecognizer(getApplication())
    private val finish : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageItemsRepository() as FirebaseDatabaseRepository<BaseItem>

        finish.value = false
    }

    internal fun removeFocus() {
        textBlockController.removeFocus(getApplication())
        imageBlockController.removeImageFocus()
    }

    internal fun createTextBlockController() {
        textBlockController.createController(getApplication())
        imageBlockController.createController(getApplication())
    }

    internal fun createTextBlock(layerWidth: Int, textLayout: RelativeLayout){
        disableDraw()
        imageBlockController.removeImageFocus()
        textLayout.addView(textBlock.createTextBlock(getApplication(), checkMaxHeight(), layerWidth))
        showTextFormatTools()
    }

    internal fun createImageBlock(imageLayout: RelativeLayout, resultCode: Int, data: Intent?) {
        disableDraw()
        textBlockController.removeFocus(getApplication())
        val imageView: ImageView? = imageBox.setImage( resultCode, data, getApplication(), imageBox.createImageBlock(getApplication(), checkMaxHeight(),imageLayout.width))
        if(imageView != null)
            imageLayout.addView(imageView)
    }

    internal fun showTextFormatTools (){
        val mainLayer = textBlockController.controllerLayout.parent.parent.parent.parent as LinearLayout
        val textFormatTools = mainLayer.format_tools
        val noteTools = mainLayer.note_tools
        textFormatTools.visibility = View.VISIBLE
        noteTools.visibility = View.GONE
    }

    internal fun hideTextFormatTools (){
        val mainLayer = textBlockController.controllerLayout.parent.parent.parent.parent as LinearLayout
        val textFormatTools = mainLayer.format_tools
        val noteTools = mainLayer.note_tools
        textFormatTools.visibility = View.GONE
        noteTools.visibility = View.VISIBLE
    }

    internal fun addUrlForDelete(url: String){
        if(url != "")
            urlList.add(url)
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
                        p.leftMargin = pointX.toInt() - Math.abs(width) - 6
                    else p.leftMargin = pointX.toInt()

                    if (height < 0)
                        p.topMargin = pointY.toInt() - Math.abs(height) - 6
                    else {
                        p.topMargin = pointY.toInt()
                    }

                    p.height = Math.abs(height)
                    p.width = Math.abs(width)

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

        repository.removePageItems(pageId)
        deleteImage()

        var imageChildCount = imageLayer.childCount
        for (i in 0 until imageChildCount){
            val v = imageLayer.getChildAt(i)
            if(v is ImageView){
                saveImage(v)
            }
            if(v is ShapeView){
                saveShape(v)
            }
        }

        savePaint(paintLayer)

        var textChildCount = textLayer.childCount
        for (i in 0 until textChildCount){
            val v = textLayer.getChildAt(i) as EditText
            saveText(v)
        }

        val stream = ByteArrayOutputStream()
        noteBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        if(pageContent != null){
            deleteFile(pageContent!!)
            pageContent = null
        }
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis().toString()+".png")
            storageReference.putBytes(byteArray).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {

                repository.changePageContent(it.toString(), paintLayer.height, paintLayer.width, pageId, conspId)
                pageContent = it.toString()
            }
        }
    }

    private fun saveText(v: EditText) { // this: CoroutineScope
        if(v.text.toString() != ""){
            var item = PageItem("", Html.toHtml(v.text), "EditText", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))

            repository.saveNewItem(item, pageId)

            val bitmap = loadBitmapFromView(v)
            val canvas = Canvas(noteBitmap)
            canvas.drawBitmap(bitmap,  v.x , v.y , null)
        }
    }

    private fun saveImage(v: ImageView) { // this: CoroutineScope
//        val separated = v.transitionName!!.split(";")
        val bitmap = loadBitmapFromView(v)
        if(v.transitionName != ""){
            var item = PageItem("", v.transitionName, "ImageView", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))
            repository.saveNewItem(item, pageId)
        }
        else {
//            val myUri = Uri.parse(separated[0])
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis().toString() + ".png")

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            storageReference.putBytes(byteArray).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    var item = PageItem("", it.toString(), "ImageView", pxToDp(v.width.toFloat(), getApplication()),
                        pxToDp(v.height.toFloat(), getApplication()),
                        pxToDp(v.x, getApplication()),
                        pxToDp(v.y, getApplication()))
//                    myRef.push().setValue(item)
                    repository.saveNewItem(item, pageId)
                    v.transitionName = v.transitionName+item.content

                }

            }
        }
        val canvas = Canvas(noteBitmap)
        canvas.drawBitmap(bitmap,  v.x , v.y , null)

    }

//    fun getFileExtention(uri: Uri, context: Context): String{
//        val contentResolver = context.contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
//    }

    private fun saveShape(v: ShapeView){ // this: CoroutineScope
        if(v.width!=0 && v.height!=0){
            val content: String = "shapeType:"+v.shapeType+";isFillColor:"+v.isFillColor+";strokeWidth:"+v.strokeWidth+";fillColor:"+v.fillColor+";strokeColor:"+v.strokeColor
            var item = PageItem("", content, "ShapeView", pxToDp(v.width.toFloat(), getApplication()),
                pxToDp(v.height.toFloat(), getApplication()),
                pxToDp(v.x, getApplication()),
                pxToDp(v.y, getApplication()))

            repository.saveNewItem(item, pageId)
            val bitmap = loadBitmapFromView(v)
            val canvas = Canvas(noteBitmap)
            canvas.drawBitmap(bitmap,  v.x , v.y , null)
        }
    }
    private fun savePaint(paintLayer: SimpleDrawingView){ // this: CoroutineScope
        val bitmap = paintLayer.mBitmap
        var item = PageItem("", bitMapToString(bitmap), "PaintView", paintLayer.width.toFloat(), paintLayer.height.toFloat(), 0f, 0f)

        repository.saveNewItem(item, pageId)
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
            fragment.shape_preview.fillColor = Color.parseColor("#646464")
            fragment.shape_preview.strokeColor = Color.parseColor("#000000")
            checkMaxHeight()
            progressBar.visibility = View.GONE
            isLoaded = true
        }
    }

    private fun loadEditText(textLayout: RelativeLayout, item: PageItem){
        textLayout.addView(textBlock.loadTextBlock(getApplication(), Html.fromHtml(item.content), dpToPx(item.width!!, getApplication()) , dpToPx(item.height!!, getApplication()) , dpToPx(item.x!!, getApplication()) , dpToPx(item.y!!, getApplication()) ))
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

    internal fun startVoiceRecognicion(context: Context, voiceProgressBar: ProgressBar, textLayout: RelativeLayout){
        setupPermissions (context)
        speechRecognizer!!.setRecognitionListener(VoiceListener(voiceProgressBar))

        if(isRecogniserActive){

            voiceProgressBar.visibility = View.GONE
            speechRecognizer!!.stopListening()
        }
        else{

            if (textBlockController.controllerLayout.visibility != View.VISIBLE){
                createTextBlock(textLayout.width, textLayout)
            }
            voiceProgressBar.visibility = View.VISIBLE
            startRecognizer(context)
        }
        isRecogniserActive = !isRecogniserActive
    }

    private fun startRecognizer(context : Context){

            val myIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            myIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ConfigurationCompat.getLocales(context.resources.configuration)[0])
            speechRecognizer!!.startListening(myIntent)

    }
    
    internal inner class VoiceListener(val voiceProgressBar: ProgressBar) : RecognitionListener {
        var selectionStart = textBlockController.viewBlock.selectionStart
        override fun onReadyForSpeech(params: Bundle?) {
        }

        override fun onRmsChanged(rmsdB: Float) {
            voiceProgressBar.progress = rmsdB.toInt()
        }

        override fun onBufferReceived(buffer: ByteArray?) {
        }

        override fun onPartialResults(partialResults: Bundle?) {

        }

        override fun onEvent(eventType: Int, params: Bundle?) {
        }

        override fun onBeginningOfSpeech() {
            selectionStart = textBlockController.viewBlock.selectionStart
        }

        override fun onEndOfSpeech() {
            voiceProgressBar.visibility = View.GONE
            isRecogniserActive  = false
        }

        override fun onError(error: Int) {
            voiceProgressBar.visibility = View.GONE
            isRecogniserActive  = false
        }

        override fun onResults(results: Bundle?) {
            val data = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            var text = StringBuilder(textBlockController.viewBlock.text).insert(selectionStart, data[0]).toString()
            textBlockController.viewBlock.setText(text, TextView.BufferType.EDITABLE)
            textBlockController.viewBlock.setSelection(selectionStart + data[0].toString().length )
        }

    }

    private fun setupPermissions(context: Context) {
        val permission = ContextCompat.checkSelfPermission((context as Activity),
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest(context)
        }
    }

    private fun makeRequest(context: Context) {
        ActivityCompat.requestPermissions((context as Activity),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQUEST_CODE)
    }

    companion object {
        private val TAG = "MyStt3Activity"
        private val RECORD_REQUEST_CODE = 101
    }

    fun setSpan (span: String, context: Context){

        var spanType =  Spannable.SPAN_INCLUSIVE_INCLUSIVE

        when(span){
            "bold" -> {

                var styleSpans: Array<StyleSpan> = textBlockController.viewBlock.text.getSpans(textBlockController.viewBlock.selectionStart,
                    textBlockController.viewBlock.selectionEnd,
                    StyleSpan::class.java)
                if(styleSpans.isEmpty()){
                    textBlockController.viewBlock.text.setSpan(
                        StyleSpan(Typeface.BOLD),
                        textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                        spanType)
                }
                else
                {
                    for (span in styleSpans){
                        if(span.style == Typeface.BOLD)
                            textBlockController.viewBlock.text.removeSpan(span)
                    }
                }
            }
            "italic" -> {
                var styleSpans: Array<StyleSpan> = textBlockController.viewBlock.text.getSpans(textBlockController.viewBlock.selectionStart,
                    textBlockController.viewBlock.selectionEnd,
                    StyleSpan::class.java)
                if(styleSpans.isEmpty()){
                    textBlockController.viewBlock.text.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                        spanType)
                }
                else
                {
                    for (span in styleSpans){
                        if(span.style == Typeface.ITALIC)
                            textBlockController.viewBlock.text.removeSpan(span)
                    }
                }
            }
            "underline" -> {

                var ulSpan: Array<UnderlineSpan> = textBlockController.viewBlock.text.getSpans(textBlockController.viewBlock.selectionStart,
                    textBlockController.viewBlock.selectionEnd,
                    UnderlineSpan::class.java)
                if(ulSpan.isEmpty()){
                    textBlockController.viewBlock.text.setSpan(
                        UnderlineSpan(),
                        textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                        spanType)
                }
                else
                {
                    for (span in ulSpan){
                        textBlockController.viewBlock.text.removeSpan(span)
                    }
                }
            }
            "strikethrough" -> {

                var stSpan: Array<StrikethroughSpan> = textBlockController.viewBlock.text.getSpans(textBlockController.viewBlock.selectionStart,
                    textBlockController.viewBlock.selectionEnd,
                    StrikethroughSpan::class.java)
                if(stSpan.isEmpty()){
                    textBlockController.viewBlock.text.setSpan(
                        StrikethroughSpan(),
                        textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                        spanType)
                }
                else
                {
                    for (span in stSpan){
                        textBlockController.viewBlock.text.removeSpan(span)
                    }
                }
            }
            "text color" -> {
                pickColor(context, span)
            }
            "fill color" -> {
                pickColor(context, span)
            }
            "align left" -> {
                textBlockController.viewBlock.gravity = Gravity.LEFT
            }
            "align right" -> {
                textBlockController.viewBlock.gravity = Gravity.RIGHT
            }
            "align center" -> {
                textBlockController.viewBlock.gravity = Gravity.CENTER_HORIZONTAL
            }
        }
    }

    var currentColor = Color.WHITE

    private fun pickColor (context: Context, span: String){

        ColorPickerDialogBuilder
            .with(context)
            .setTitle("Choose color")
            .initialColor(currentColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener(OnColorSelectedListener() {
            })
            .setPositiveButton("Ok",  ColorPickerClickListener() { dialogInterface: DialogInterface, selectedColor: Int, ints: Array<Int> ->
                when(span){
                    "text color" -> {
                        textBlockController.viewBlock.text.setSpan(
                            ForegroundColorSpan(selectedColor),
                            textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                    "fill color" -> {
                        textBlockController.viewBlock.text.setSpan(
                            BackgroundColorSpan(selectedColor),
                            textBlockController.viewBlock.selectionStart, textBlockController.viewBlock.selectionEnd,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                currentColor = selectedColor
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface: DialogInterface, which: Int ->

            })
            .build()
            .show()
    }

    fun renamePage(name : String){
        repository.renamePage(name, pageId, conspId)
    }

    fun deleteImage(){
        for(url in urlList){
            deleteFile( url)
        }
    }

    fun deletePage(){
        val canvasLayer = textBlockController.controllerLayout.parent.parent as RelativeLayout
        val imageLayer = canvasLayer.image_layer

        var imageChildCount = imageLayer.childCount
        for (i in 0 until imageChildCount){
            val v = imageLayer.getChildAt(i)
            if(v is ImageView){
                if( v.transitionName != ""){
                    deleteFile( v.transitionName)
                }
            }
        }
        deleteImage()
        if(pageContent != null){
            deleteFile(pageContent!!)
        }
        repository.removePageItems(pageId)
        repository.removePage(pageId, conspId)

        finish.value = true
    }

    fun deleteFile(url: String){
        val contentRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        contentRef.delete()
    }

    fun getFinish(): LiveData<Boolean>{
       return finish
    }

    internal fun changeBorderColor(colorButton: ImageButton, paintLayer: SimpleDrawingView, shape_preview:ShapePreview, border_colors: LinearLayout, context: Context){
        val drawable = colorButton.drawable as ColorDrawable
        shape_preview.strokeColor = drawable.color
        paintLayer.mPaint.color = drawable.color
        shape_preview.postInvalidate()
        selectColorButton(colorButton, border_colors, context)
    }

    internal fun changeBackgroundColor(colorButton: ImageButton, shape_preview: ShapePreview, color_fill_btn: ImageButton, shape_colors: LinearLayout, context: Context): ImageButton{
        if(!shape_preview.isFillColor)
        {
            color_fill_btn.setBackgroundResource( R.drawable.border)
            shape_preview.isFillColor =!shape_preview.isFillColor
        }
        val drawable = colorButton.drawable as ColorDrawable
        shape_preview.fillColor = drawable.color
        shape_preview.postInvalidate()
        selectColorButton(colorButton, shape_colors, context)
        return colorButton
    }

    internal fun selectColorButton(colorButton: ImageButton?, linearLayout: LinearLayout, context: Context){
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

    internal fun selectShapeButton(shapeButton: ImageButton?, linearLayout: LinearLayout){
        var childCount = linearLayout.childCount
        for (i in 0 until childCount){
            val v = linearLayout.getChildAt(i)
            v.setBackgroundColor(Color.parseColor("#fafafa"))
        }
        shapeButton?.setBackgroundResource( R.drawable.btn_selected_border)

    }

}