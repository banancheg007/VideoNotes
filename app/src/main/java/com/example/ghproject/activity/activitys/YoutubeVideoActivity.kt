package com.example.ghproject.activity.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.ghproject.R
import com.example.ghproject.activity.MyYoutubeService
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_youtube_video.*

class YoutubeVideoActivity : AppCompatActivity(){

//    private val youtubeService: MyYoutubeService = MyYoutubeService()
//lateinit var mPlayer: YouTubePlayer
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_youtube_video)
////        video_relative_layout.layoutParams.height = youtube_view.height + seekBar.height/2
////        video_relative_layout.requestLayout()
//
////        val customUiView = youtube_view.inflateCustomPlayerUI(R.layout.custom_player_ui)
//
////        val uiController = youtube_view.getPlayerUIController()
////        uiController.showVideoTitle(false)
////        uiController.showYouTubeButton(false)
//
//        play_video_btn.setOnClickListener {
//            youtube_view.initialize(youtubeService.apiKey, this)
//        }
//        rewind_video_btn.setOnClickListener {
//            if(::mPlayer.isInitialized){
////                val lengthPlayed = mPlayer.durationMillis * progress / 100
//                mPlayer.seekToMillis(50000)
//            }
//        }
//        show_btn.setOnClickListener {
//            video_relative_layout.layoutParams.height = youtube_view.height + seekBar.height/2
//            video_relative_layout.requestLayout()
//            video_relative_layout.bringToFront()
//            video_relative_layout.visibility = View.VISIBLE
//        }
//
//    }
//
//    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
//        mPlayer = p1!!
//        p1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
//        p1!!.loadVideo("a4DL4I99j5U")
//    }
//
//
//
//    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
//        Log.e("myApp", "onInitializationFailure")
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_video)


    }
}
