package com.example.ghproject.activity.viewmodels

import android.app.Application
import android.view.View
import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.ghproject.R
import com.example.ghproject.activity.entities.Note
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.*
import kotlin.math.roundToInt
import android.view.ViewGroup

class VideoViewModel (application: Application) : BaseViewModel(application) {
    var isSeekBarInTouch = false
    var isVideoOnFocus = false
    var wait = 0
    var isPause = false
    var videoDuration = 0f
    var horizontalMax = -10
    var verticalPosition: Short = 0
    lateinit var youTubePlayer: YouTubePlayer
    val allNotes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    init {
        val note1 = Note ("Note 1", 10)
        val note2 = Note ("Note 2", 100)
        val note3 = Note ("Note 3", 380)
        val note4 = Note ("Note 4", 1100)
        val note5 = Note ("Note 5", 1800)
        val note6 = Note ("Note 6", 3350)
        val notesList: List<Note> = listOf(note1, note2, note3, note4, note5, note6)
        allNotes.value = notesList

    }

    fun getAllNotes(): LiveData<List<Note>> {
        return allNotes
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun showPlayerUI(custom_ui: RelativeLayout, video_progressBar : ProgressBar, context: Context){
        if(!isVideoOnFocus){
            custom_ui.visibility = View.VISIBLE
            video_progressBar.visibility = View.GONE
            wait = 0
            waitBeforeCloseAsync(custom_ui, video_progressBar, context)
        }
    }

    private fun waitBeforeCloseAsync(custom_ui: RelativeLayout, video_progressBar : ProgressBar, context: Context)= GlobalScope.async { // this: CoroutineScope
        launch {
            while (wait < 4){
                if(!isSeekBarInTouch)
                    wait++
                delay(1000)
            }
            isVideoOnFocus = false
            (context as Activity).runOnUiThread {
                custom_ui.visibility = View.GONE
                video_progressBar.visibility = View.VISIBLE
            }
        }
    }

    fun setPause(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, pause_btn: ImageButton){
        if(!isPause){
            youTubePlayer.pause()
            pause_btn.setImageResource(R.drawable.ic_play_arrow)
            isPause = true
            wait = 0

        }
        else {
            youTubePlayer.play()
            pause_btn.setImageResource(R.drawable.ic_pause)
            isPause = false
            wait = 0
        }
    }

    fun setVideoDuration(video_seekBar: SeekBar, video_progressBar: ProgressBar, video_player_rl: RelativeLayout, video_duration: TextView, duration: Float, marks_rl: RelativeLayout, notes_list_recycler_view: RecyclerView, width: Int){
        if(videoDuration == 0f){
            video_seekBar.max = duration.toInt()
            video_progressBar.max = duration.toInt()
            video_player_rl.visibility = View.VISIBLE
            convertTime (duration)
            video_duration.text = convertTime(duration)
            videoDuration = duration
            createMark(marks_rl, notes_list_recycler_view, width)
        }

    }

    fun setCurrentSecond(video_seekBar : SeekBar, video_progressBar : ProgressBar, second : Float){
        if(!isSeekBarInTouch){
            video_seekBar.progress = second.toInt()
        }
        video_progressBar.progress = second.toInt()
//        setMark (tracker.currentSecond.toInt(), tracker.videoDuration, width)
    }

    internal fun convertTime (time: Float): String{
        val ours = (time/3600).toInt()
        var minutes = (time/60).toInt()
        val seconds = time.toInt() - 60*minutes
        minutes -=60*ours
        var string = ""
        if(ours != 0) {
            string = "$ours:"
            if(minutes < 10)
                string += 0
        }
        string = "$string$minutes:"
        if(seconds < 10)
            string += 0
        string = "$string$seconds"
        return string
    }

    fun load()= GlobalScope.async { // this: CoroutineScope
        launch {
            delay(100)
            isSeekBarInTouch = false
        }
    }

    fun seekBarChange(fromUser : Boolean){
        if(fromUser)
            wait = 0
    }

    fun setMark(time: Int, width: Int, timeMark: LinearLayout, markLine: ImageView) {

        val newWidth = width - dpToPx(12f, getApplication())
        val pxInSec = newWidth/videoDuration
        val params = timeMark.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = (pxInSec*time).roundToInt() - dpToPx(2.3f, getApplication())
        if(horizontalMax>=params.leftMargin){
//            if(vertikalPosition == 1.toShort()){
//                markLine.layoutParams.height = dpToPx(2f, getApplication())}
//            if(vertikalPosition == 2.toShort()){
//
//            }
            when ( verticalPosition){
                1.toShort() -> {
                    markLine.layoutParams.height = dpToPx(32f, getApplication())
                    verticalPosition = 3
                    sendViewToBack(timeMark)
                    return
                }
                2.toShort() -> {
                    markLine.layoutParams.height = dpToPx(2f, getApplication())
                    verticalPosition = 1
                    timeMark.bringToFront()
                    return
                }
                3.toShort() -> {
                    markLine.layoutParams.height = dpToPx(16f, getApplication())
                    verticalPosition = 2
                    return
                }
                else -> {
                    return
                }
            }
        }
        else verticalPosition = 2
        timeMark.layoutParams = params
        horizontalMax = params.leftMargin + dpToPx(16f, getApplication())
    }

    fun createMark (marks_rl: RelativeLayout, recyclerView: RecyclerView, width: Int){
        if(videoDuration != 0f) {
            for (i in 0 until allNotes.value!!.size){
                val markLayout = LinearLayout(getApplication())
                val markLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                markLayout.layoutParams = markLayoutParams
                markLayout.orientation = LinearLayout.VERTICAL
                markLayout.gravity = Gravity.CENTER_HORIZONTAL
                val markLine = ImageView(getApplication(), null, R.style.TimeMarkLine)
                val markLineParams =
                    RelativeLayout.LayoutParams(dpToPx(2f, getApplication()), dpToPx(18f, getApplication()))
                markLine.layoutParams = markLineParams
                markLine.setBackgroundResource(R.color.accent_dark)
                val markButton = ImageButton(getApplication(), null, R.style.TimeMarkButton)
                val markButtonParams =
                    RelativeLayout.LayoutParams(dpToPx(16f, getApplication()), dpToPx(16f, getApplication()))
                markButton.layoutParams = markButtonParams
                markButton.setBackgroundResource(R.drawable.note_mark_btn)
                markButton.setOnClickListener {
                    recyclerView.scrollToPosition(i)
                    youTubePlayer.seekTo(allNotes.value!![i].time.toFloat())
                }

                markLayout.addView(markLine)
                markLayout.addView(markButton)
                marks_rl.addView(markLayout)

                setMark(allNotes.value!![i].time, width, markLayout, markLine)
            }

        }
    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup
        if (null != parent) {
            parent.removeView(child)
            parent.addView(child, 0)
        }
    }
}