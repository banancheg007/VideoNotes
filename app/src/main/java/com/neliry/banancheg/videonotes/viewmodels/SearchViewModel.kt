package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.entities.VideoItem
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.neliry.banancheg.videonotes.activities.PlayerActivity

class SearchViewModel(application: Application): BaseViewModel(application), OnViewClickListener {
    val myAdapter: FirebaseAdapter by lazy { FirebaseAdapter(this) }
    val navigationEvent = LiveMessageEvent<ViewNavigation>()
    override fun onViewClicked(view: View?, any: Any?) {
        val intent = Intent(getApplication(), PlayerActivity::class.java)
        intent.putExtra("VIDEO_ITEM", any as VideoItem)
       // intent.putExtra("list", list)
        navigationEvent.sendEvent { startActivityForResult(intent, 1) }
    }

    var list:List<VideoItem>? = null

    val  handler = Handler()
    private  var searchResults: MutableLiveData<List<VideoItem>> = MutableLiveData()

    fun getSearchResults():LiveData<List<VideoItem>>{
        return searchResults
    }

    fun searchOnYoutube(keywords: String) {
        object : Thread() {
            override fun run() {
                val yc = YoutubeConnector(getApplication())
                list = yc.search(keywords)!!
                handler.post { searchResults.value = list}
            }
        }.start()
    }
}