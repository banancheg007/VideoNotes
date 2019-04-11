package com.neliry.banancheg.videonotes.activities


import android.os.Bundle
import android.view.View
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayer

import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.google.android.youtube.player.YouTubePlayerView
import com.neliry.banancheg.videonotes.entities.VideoItem
import com.neliry.banancheg.videonotes.R
import kotlinx.android.synthetic.main.activity_player.*
import android.content.Intent




class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private lateinit var currentVideo: VideoItem
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_confirm_search_activity->{
                val intent = Intent()
                intent.putExtra("VIDEO_ITEM", currentVideo)
                setResult(1, intent)
                finish()
            }
            R.id.button_cancel_search_activity->{
                finish()
            }
        }
    }

    private var playerView: YouTubePlayerView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.neliry.banancheg.videonotes.R.layout.activity_player)

        playerView = findViewById(com.neliry.banancheg.videonotes.R.id.player_view)
        playerView!!.initialize(YoutubeConnector.KEY, this)
//        val list = intent.getSerializableExtra("list") as List<BaseItem>
//        /*for(item in list){
//            Log.d("myTag", item)
//        }*/
//        val adapter = FireBaseCustomSpinnerAdapter(this, android.R.layout.simple_spinner_item,list )
//        //val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        val spinner = findViewById<Spinner>(R.id.spinner)
//        spinner.adapter = adapter
//        // заголовок
//        spinner.prompt = "Choose parent theme"
//        val currentItem= spinner.selectedItem as BaseItem
//        Log.d("myTag", currentItem.name)
        // выделяем элемент
        //spinner.setSelection(2)
       button_confirm_search_activity.setOnClickListener(this)
        button_cancel_search_activity.setOnClickListener(this)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
        if (!b) {
            currentVideo = intent.getSerializableExtra("VIDEO_ITEM") as VideoItem

            //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
            youTubePlayer.setShowFullscreenButton(false)
            //youTubePlayer.cueVideo(videoItem.id)
            youTubePlayer.loadVideo(currentVideo.id)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
        Toast.makeText(this, "Initialization Failed", Toast.LENGTH_LONG).show()
    }
}
