package com.neliry.banancheg.videonotes.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayer

import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.google.android.youtube.player.YouTubePlayerView




class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private var playerView: YouTubePlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.neliry.banancheg.videonotes.R.layout.activity_player)

        playerView = findViewById(com.neliry.banancheg.videonotes.R.id.player_view) as YouTubePlayerView
        playerView!!.initialize(YoutubeConnector.KEY, this)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
        if (!b) {
            youTubePlayer.cueVideo(intent.getStringExtra("VIDEO_ID"))
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
        Toast.makeText(this, "Initialization Failed", Toast.LENGTH_LONG).show()
    }
}
