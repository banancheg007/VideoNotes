package com.neliry.banancheg.videonotes.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.entities.VideoItem
import com.neliry.banancheg.videonotes.viewmodels.SearchViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), ViewNavigation {


    private lateinit var searchViewModel: SearchViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val layoutManager =  LinearLayoutManager(this)
        recycler_view_videos.layoutManager = layoutManager

        search_input.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.searchOnYoutube(v.text.toString())
                return@OnEditorActionListener false
            }
            true
        })



        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.navigationEvent.setEventReceiver(this, this)

        searchViewModel.getSearchResults().observe(this, Observer<List<VideoItem>> {
                searchResults-> if (searchResults!= null){
           // recycler_view_videos.addItemDecoration(ItemDecorator(20))
            recycler_view_videos.adapter = searchViewModel.myAdapter
            (recycler_view_videos.adapter as FirebaseAdapter).setItems(searchResults)
        }
        })
    }







    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        val videoItem = data.getSerializableExtra("VIDEO_ITEM") as VideoItem
        Log.d("myTag", "video id " + videoItem.id)
        val intent = Intent()
        intent.putExtra("VIDEO_ITEM", videoItem)
        setResult(1, intent)
        finish()
    }
}
