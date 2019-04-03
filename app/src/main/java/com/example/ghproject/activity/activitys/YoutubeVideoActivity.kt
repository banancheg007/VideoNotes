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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ghproject.activity.viewmodels.VideoViewModel
import com.example.ghproject.activity.viewmodels.ViewModelFactory
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class YoutubeVideoActivity : AppCompatActivity(){

    private val videoViewModel: VideoViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get( VideoViewModel::class.java)}
    lateinit var adapter: NotesListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_video)

        video_player_rl.visibility = View.GONE

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val tracker = YouTubePlayerTracker()
        youtube_player_view.inflateCustomPlayerUi(R.layout.video_player_custom_ui)

        adapter = NotesListAdapter(width - dpToPx(30f, this@YoutubeVideoActivity))
        notes_list_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        notes_list_recycler_view.adapter = adapter

        videoViewModel!!.getAllNotes().observe(this, Observer { notes ->
            adapter.setMessages(notes)
            videoViewModel.createMark(marks_rl, notes_list_recycler_view, width)
        })



        video_player_rl.setOnClickListener {
            videoViewModel.showPlayerUI(custom_ui, video_progressBar, this@YoutubeVideoActivity)
        }

        custom_ui.setOnClickListener {
            custom_ui.visibility = View.GONE
            video_progressBar.visibility = View.VISIBLE
            videoViewModel.wait = 4
        }

        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoViewModel.youTubePlayer = youTubePlayer
                video_player_rl.layoutParams.height = youtube_player_view.height+dpToPx(8f, this@YoutubeVideoActivity)
                video_player_rl.requestLayout()
                youTubePlayer.addListener(tracker)
                video_player_rl.bringToFront()
                video_seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        current_time.text = videoViewModel.convertTime(progress.toFloat())
                        video_progressBar.progress = progress

                        videoViewModel.seekBarChange(fromUser)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        videoViewModel.isSeekBarInTouch = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        youTubePlayer.seekTo(seekBar.progress.toFloat())

                        videoViewModel.load()
                    }
                })

                pause_btn.setOnClickListener {
                    videoViewModel.setPause(youTubePlayer, pause_btn)
                }
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                videoViewModel.setVideoDuration(video_seekBar, video_progressBar, video_player_rl, video_duration, duration, marks_rl, notes_list_recycler_view, width)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                videoViewModel.setCurrentSecond(video_seekBar, video_progressBar, second)
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                loading_progressBar.progress = (loadedFraction*100).toInt()
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
            }
        })
    }


    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}
