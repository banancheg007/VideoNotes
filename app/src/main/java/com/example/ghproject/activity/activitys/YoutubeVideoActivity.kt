package com.example.ghproject.activity.activitys

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ghproject.activity.adapters.NotesListAdapter
import kotlinx.android.synthetic.main.activity_youtube_video.*
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import com.example.ghproject.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class YoutubeVideoActivity : AppCompatActivity(){

    lateinit var adapter: NotesListAdapter
    var isSeekBarInTouch = false
    var isVideoOnFocus = false
    var wait = 0
    var isPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_video)

        video_player_rl.visibility = View.GONE

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        adapter = NotesListAdapter(width - dpToPx(30f, this@YoutubeVideoActivity))
        notes_list_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        notes_list_recycler_view.adapter = adapter

        val tracker = YouTubePlayerTracker()
        val customPlayerUi = youtube_player_view.inflateCustomPlayerUi(R.layout.video_player_custom_ui)

        video_player_rl.setOnClickListener {
            if(!isVideoOnFocus){
                custom_ui.visibility = View.VISIBLE
                video_progressBar.visibility = View.GONE
                wait = 0
                waitBeforeClose()
            }
        }

        custom_ui.setOnClickListener {
            custom_ui.visibility = View.GONE
            video_progressBar.visibility = View.VISIBLE
            wait = 3
        }

        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                video_player_rl.layoutParams.height = youtube_player_view.height+dpToPx(8f, this@YoutubeVideoActivity)
                video_player_rl.requestLayout()
                video_player_rl.bringToFront()

                youTubePlayer.addListener(tracker)

                video_seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        current_time.text = convertTime(progress.toFloat())
                        video_progressBar.progress = progress
                        if(fromUser)
                            wait = 0
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        isSeekBarInTouch = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        youTubePlayer.seekTo(seekBar.progress.toFloat())

                        load()
                    }
                })

                pause_btn.setOnClickListener {
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
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                video_seekBar.max = duration.toInt()
                video_progressBar.max = duration.toInt()
                video_player_rl.visibility = View.VISIBLE
                convertTime (duration)
                video_duration.text = convertTime(duration)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                if(!isSeekBarInTouch){
                    video_seekBar.progress = second.toInt()
                }
                video_progressBar.progress = second.toInt()
                setMark (tracker.currentSecond.toInt(), tracker.videoDuration, width)
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                loading_progressBar.progress = (loadedFraction*100).toInt()
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
            }
        })
    }

    fun convertTime (time: Float): String{
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

    fun waitBeforeClose()= GlobalScope.async { // this: CoroutineScope
        launch {
            while (wait < 3){
                if(!isSeekBarInTouch)
                wait++
                delay(1000)
            }
            isVideoOnFocus = false
            runOnUiThread {
                custom_ui.visibility = View.GONE
                video_progressBar.visibility = View.VISIBLE
            }
        }
    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    fun setMark(time: Int, duration: Float, width: Int) {
        val newWidth = width - dpToPx(12f, this@YoutubeVideoActivity)
        val pxInSec = newWidth/duration

        val params = time_mark.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = (pxInSec*time).roundToInt() - dpToPx(2.3f, this@YoutubeVideoActivity)
        time_mark.layoutParams = params
    }
}
