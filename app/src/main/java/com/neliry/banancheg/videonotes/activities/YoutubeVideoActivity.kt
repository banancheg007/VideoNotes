package com.neliry.banancheg.videonotes.activities

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.graphics.Point
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.NotesListAdapter
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.viewmodels.ViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlinx.android.synthetic.main.activity_youtube_video.*


class YoutubeVideoActivity : AppCompatActivity(){

    private val videoViewModel: VideoViewModel by lazy { ViewModelProviders.of(this, ViewModelFactory(application)).get( VideoViewModel::class.java)}
    lateinit var adapter: NotesListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_video)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val tracker = YouTubePlayerTracker()
        val intent: Intent = intent

        youtube_player_view.inflateCustomPlayerUi(R.layout.custom_player_ui)

        val videoUrl: String = videoViewModel.parseIntent(intent, supportActionBar!!)!!

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = NotesListAdapter(width - dpToPx(30f, this@YoutubeVideoActivity))
        notes_list_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        notes_list_recycler_view.addItemDecoration(
            NoteListDecoration(
                this
            )
        )

        videoViewModel.getItems().observe(this, Observer { notes ->
            Log.d("myTag", "yob tvoyu mat")
            adapter.setNotes(notes)
            notes_list_recycler_view.adapter = adapter
            videoViewModel.allNotes =  videoViewModel.getItems() as MutableLiveData<List<Page>>
            videoViewModel.createMark(marks_rl, notes_list_recycler_view, pause_btn)
        })

        video_player_rl.setOnClickListener {
            videoViewModel.showPlayerUI(custom_ui, video_progressBar, this@YoutubeVideoActivity)
        }

        custom_ui.setOnClickListener {
            custom_ui.visibility = View.GONE
            video_progressBar.visibility = View.VISIBLE
            videoViewModel.wait = 4
        }
        youtube_player_view.enableAutomaticInitialization
        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoUrl, 0f)
                youTubePlayer.play()
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
                    videoViewModel.setPause(pause_btn)
                }
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                videoViewModel.setVideoDuration(video_seekBar, video_progressBar, custom_ui, video_duration, duration, marks_rl, notes_list_recycler_view, pause_btn, this@YoutubeVideoActivity)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                videoViewModel.setCurrentSecond(video_seekBar, video_progressBar, second)
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                loading_progressBar.progress = (loadedFraction*100).toInt()
            }

            override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality
            ) {
                super.onPlaybackQualityChange(youTubePlayer, playbackQuality)
                Log.i("onPlaybackQualityChange", playbackQuality.name)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.player_activity_menu, menu)
        return true
    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}
