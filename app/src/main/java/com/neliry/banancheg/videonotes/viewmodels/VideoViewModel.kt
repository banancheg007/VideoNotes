package com.neliry.banancheg.videonotes.activities

import android.app.Application
import android.view.View
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.NotesListAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.viewmodels.BaseNavigationDrawerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class VideoViewModel (application: Application): BaseNavigationDrawerViewModel(application), OnViewClickListener {

    var isSeekBarInTouch = false
    var isVideoOnFocus = false
    var wait = 0
    var isPause = true
    var videoDuration = 0f
    var horizontalMax = -10
    var verticalPosition: Short = 0
    lateinit var youTubePlayer: YouTubePlayer
    lateinit var allNotes: List<Page>
    lateinit var adapter: NotesListAdapter
    lateinit var conspId: String
    var currentTime = 0f

    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageRepository() as FirebaseDatabaseRepository<BaseItem>
    }

    override fun onViewClicked(view: View?, any: Any?) {
        youTubePlayer.pause()
        val currentClickedPage = any as Page
        navigationEvent.sendEvent {  val intent = Intent(getApplication(), EditorActivity::class.java)
            intent.putExtra("currentPage", currentClickedPage)
            intent.putExtra("conspId", conspId)
            navigationEvent.sendEvent{ startActivity(intent)} }

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

    fun setPause(){
        if(!isPause){
            youTubePlayer.pause()
            wait = 0

        }
        else {
            youTubePlayer.play()
            wait = 0
        }
    }

    fun stateChange (state: PlayerConstants.PlayerState, pause_btn: ImageButton){
        when (state) {
            PlayerConstants.PlayerState.PLAYING -> {
                pause_btn.setImageResource(R.drawable.ic_pause)
                isPause = false
                return
            }
            PlayerConstants.PlayerState.PAUSED -> {
                pause_btn.setImageResource(R.drawable.ic_play_arrow)
                isPause = true
                return
            }
            else -> return
        }
    }

    fun setVideoDuration(video_seekBar: SeekBar, video_progressBar: ProgressBar, custom_ui: RelativeLayout, video_duration: TextView, duration: Float, marks_rl: RelativeLayout, notes_list_recycler_view: RecyclerView, context: Context){
        if(videoDuration == 0f){
            youTubePlayer.pause()
            video_seekBar.max = duration.toInt()
            video_progressBar.max = duration.toInt()
            convertTime (duration)
            video_duration.text = convertTime(duration)
            videoDuration = duration
            createMark(marks_rl, notes_list_recycler_view)
            showPlayerUI(custom_ui, video_progressBar, context)
        }

    }

    fun setCurrentSecond(video_seekBar : SeekBar, video_progressBar : ProgressBar, second : Float){
        currentTime = second
        if(!isSeekBarInTouch){
            video_seekBar.progress = second.toInt()
        }
        video_progressBar.progress = second.toInt()

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

    fun getCurrentTime(cureentSecond: Float): ArrayList<Int>{
        val list = arrayListOf<Int>()
        var time: Float = cureentSecond
        val ours = (time/3600).toInt()
        time -= 60*ours
        var minutes = (time/60).toInt()
        time -= 60*minutes
        val seconds = time.toInt()

        list.addAll(listOf(ours, minutes, seconds))
        return list
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

    fun createMark (marks_rl: RelativeLayout, recyclerView: RecyclerView){
        if(videoDuration != 0f) {
            verticalPosition = 0
            horizontalMax = -10
            marks_rl.removeAllViews()
            for (i in 0 until allNotes.size){
                val markLayout = LinearLayout(getApplication())
                val markLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                markLayout.layoutParams = markLayoutParams
                markLayout.orientation = LinearLayout.VERTICAL
                markLayout.gravity = Gravity.CENTER_HORIZONTAL
                val markLine = ImageView(getApplication())
                val markLineParams =
                    RelativeLayout.LayoutParams(dpToPx(2f, getApplication()), dpToPx(18f, getApplication()))
                markLine.layoutParams = markLineParams
                markLine.setBackgroundResource(R.color.accent_lite)
                val markButton = ImageButton(getApplication())
                val markButtonParams =
                    RelativeLayout.LayoutParams(dpToPx(16f, getApplication()), dpToPx(16f, getApplication()))
                markButton.layoutParams = markButtonParams
                markButton.setBackgroundResource(R.drawable.note_mark_btn)
                markButton.setOnClickListener {
                    recyclerView.scrollToPosition(i)
                    youTubePlayer.seekTo(allNotes[i].time!!.toFloat())
                    isPause = true
                    setPause ()
                }

                markLayout.addView(markLine)
                markLayout.addView(markButton)
                marks_rl.addView(markLayout)

                setMark(allNotes[i].time!!, marks_rl.width, markLayout, markLine)
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

    fun createNewPage(name: String, timeArray: ArrayList<Int>): Boolean{
        val currentTime = timeArray[0]*3600+timeArray[1]*60+timeArray[2]
        if(currentTime<videoDuration && name != ""){
            val creationTime = getDate(System.currentTimeMillis(), "dd.MM.yyyy hh:mm a")
            val page: Page = Page (name = name, time= currentTime, creationTime= creationTime, height = 0f, width = 0f)
            repository.saveNewItem(page, conspId)
            navigationEvent.sendEvent {  val intent = Intent(getApplication(), EditorActivity::class.java)
                intent.putExtra("currentPage", page)
                intent.putExtra("conspId", conspId)
                navigationEvent.sendEvent{ startActivity(intent)} }
            return true
        }
        return false
    }

    private fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun parseIntent(intent: Intent, supportActionBar: androidx.appcompat.app.ActionBar): String?{
        if (intent.getSerializableExtra("currentConspectus") !=null) {
            val conspectus: Conspectus = intent.getSerializableExtra("currentConspectus") as Conspectus
            repository.setDatabaseReference("pages", conspectus.id.toString())
            repository.setDatabaseReferenceOrder("time")
            supportActionBar.title = conspectus.name
            conspectus.videoUrl
            conspId = conspectus.id.toString()
            return conspectus.videoUrl
        }
        return  ""
    }
}