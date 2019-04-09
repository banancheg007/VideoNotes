package com.neliry.banancheg.videonotes.views

import android.content.Intent
import android.os.Handler

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel
import com.neliry.banancheg.videonotes.viewmodels.SearchViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_theme.*
import android.widget.Spinner
import android.R.attr.data
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), ViewNavigation {
    var list= ArrayList<BaseItem>()

    lateinit var searchViewModel: SearchViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)




        search_input.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.searchOnYoutube(v.text.toString())
                return@OnEditorActionListener false
            }
            true
        })



        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.navigationEvent.setEventReceiver(this, this)
        searchViewModel.getItems().observe(this,
            Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")

                for (item in items){
                    list.add(item)
                    Log.d("myTag", item.name)
                }

                //val spinner = findViewById<Spinner>(R.id.spinner)
                //spinner.adapter = adapter
                // заголовок
                // spinner.prompt = "Change theme"
                // выделяем элемент
                //spinner.setSelection(2)
            })

        searchViewModel.getSearchResults().observe(this, Observer<List<VideoItem>> {
                searchResults-> if (searchResults!= null){
            val layoutManager =  LinearLayoutManager(this)
            recycler_view_videos.layoutManager = layoutManager
           // recycler_view_videos.addItemDecoration(ItemDecorator(20))
            recycler_view_videos.adapter = (FirebaseAdapter(searchViewModel,searchResults))
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
