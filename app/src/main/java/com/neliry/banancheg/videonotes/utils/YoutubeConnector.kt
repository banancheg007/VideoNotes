package com.neliry.banancheg.videonotes.utils

import android.content.Context
import android.util.Log
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.neliry.banancheg.videonotes.entities.VideoItem

import java.io.IOException
import java.util.ArrayList

class YoutubeConnector(context: Context) {
    private val youtube: YouTube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer { })
        .setApplicationName("VideoNotes").build()
    private var query: YouTube.Search.List? = null

    init {

        try {
            query = youtube.search().list("id,snippet")
            query?.key = KEY
            query?.maxResults = 25L
            query?.type = "video"
            query?.fields = "items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)"
        } catch (e: IOException) {
            Log.d("YC", "Could not initialize: " + e.message)
        }

    }

    fun search(keywords: String): List<VideoItem>? {
        query!!.q = keywords
        try {
            val response = query!!.execute()
            val results = response.items
            val items = ArrayList<VideoItem>()

            for (result in results) {
                val item = VideoItem()
                item.title = result.snippet.title
                item.description = result.snippet.description
                item.thumbnailURL = result.snippet.thumbnails.default.url
                item.id = result.id.videoId
                items.add(item)
            }

            return items
        } catch (e: IOException) {
            Log.d("YC", "Could not search: $e")
            return null
        }

    }

    companion object {

        val KEY = "AIzaSyBs13ByqKwTYJoW03kc71sORsvlzDhCrQA"
    }
}