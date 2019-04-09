package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.utils.YoutubeConnector

class SearchViewModel(application: Application): ThemeViewModel(application){
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