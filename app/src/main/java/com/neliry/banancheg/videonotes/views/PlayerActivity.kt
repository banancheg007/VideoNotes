package com.neliry.banancheg.videonotes.views

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayer

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.youtube.player.YouTubeInitializationResult
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.google.android.youtube.player.YouTubePlayerView
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.viewmodels.SearchViewModel
import android.widget.Spinner
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FireBaseCustomSpinnerAdapter
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_confirm_search_activity->{

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
        val list = intent.getSerializableExtra("list") as List<BaseItem>
        /*for(item in list){
            Log.d("myTag", item)
        }*/
        val adapter = FireBaseCustomSpinnerAdapter(this, android.R.layout.simple_spinner_item,list )
        //val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.adapter = adapter
        // заголовок
        spinner.prompt = "Choose parent theme"
        val currentItem= spinner.selectedItem as BaseItem
        Log.d("myTag", currentItem.name)
        // выделяем элемент
        //spinner.setSelection(2)
       button_confirm_search_activity.setOnClickListener(this)
        button_cancel_search_activity.setOnClickListener(this)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
        if (!b) {
            val videoItem = intent.getSerializableExtra("VIDEO_ITEM") as VideoItem

            //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
            youTubePlayer.setShowFullscreenButton(false)
            youTubePlayer.cueVideo(videoItem.id)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
        Toast.makeText(this, "Initialization Failed", Toast.LENGTH_LONG).show()
    }
}
