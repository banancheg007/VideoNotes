package com.neliry.banancheg.videonotes.views

import android.content.Intent
import android.os.Handler

import android.os.Bundle
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
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.utils.YoutubeConnector
import com.squareup.picasso.Picasso

class SearchActivity : AppCompatActivity() {
    private var searchInput: EditText? = null
    private var videosFound: ListView? = null
    private var handler: Handler? = null

    private var searchResults: List<VideoItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById<View>(R.id.search_input) as EditText
        videosFound = findViewById<View>(R.id.videos_found) as ListView

        handler = Handler()

        searchInput!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchOnYoutube(v.text.toString())
                return@OnEditorActionListener false
            }
            true
        })

        addClickListener()
    }

    private fun searchOnYoutube(keywords: String) {
        object : Thread() {
            override fun run() {
                val yc = YoutubeConnector(this@SearchActivity)
                searchResults = yc.search(keywords)
                handler!!.post { updateVideosFound() }
            }
        }.start()
    }

    private fun updateVideosFound() {
        val adapter = object : ArrayAdapter<VideoItem>(applicationContext, R.layout.video_item, searchResults!!) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.video_item, parent, false)
                }

                val thumbnail = convertView!!.findViewById<View>(R.id.video_thumbnail) as ImageView
                val title = convertView.findViewById<View>(R.id.video_title) as TextView
                val description = convertView.findViewById<View>(R.id.video_description) as TextView

                val searchResult = searchResults!![position]

                Picasso.with(applicationContext).load(searchResult.thumbnailURL).into(thumbnail)
                title.text = searchResult.description
                description.text = searchResult.description
                return convertView
            }
        }
        videosFound!!.adapter = adapter
    }

    private fun addClickListener() {
        videosFound!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(application, PlayerActivity::class.java)
            intent.putExtra("VIDEO_ID", searchResults!![position].id)
            startActivity(intent)
        }
    }
}
