package com.neliry.banancheg.videonotes.adapter


import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.*
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class FirebaseAdapter(private var onViewClickListener: OnViewClickListener,
                      private val list: List<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    companion object {
        const val TYPE_THEME = 0
        const val TYPE_CONSPECTUS = 1
        const val TYPE_PAGE = 2
        const val TYPE_VIDEO_ITEM = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_THEME->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.theme_item, parent, false)
                ThemeViewHolder(view)
            }
            TYPE_CONSPECTUS ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.conspectus_item, parent, false)
                ConspectusViewHolder(view)
            }
            TYPE_PAGE ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
                PageViewHolder(view)
            }
            TYPE_VIDEO_ITEM->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
                VideoItemViewHolder(view)
            }
            else-> throw NullPointerException()
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ThemeViewHolder-> holder.bind(list[position] as Theme)
            is ConspectusViewHolder -> holder.bind(list[position] as Conspectus)
            is PageViewHolder -> holder.bind(list[position] as Page)
            is VideoItemViewHolder -> holder.bind(list[position] as VideoItem)
        }
    }



    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

           if (list[position] is Theme) return TYPE_THEME
      if (list[position] is Conspectus) return TYPE_CONSPECTUS
        return if (list[position] is VideoItem) TYPE_VIDEO_ITEM
        else TYPE_PAGE

    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition] as BaseItem)
        }

        init{
            itemView.setOnClickListener(this)
        }

        internal var name: TextView = itemView.findViewById(R.id.theme_name_textview)


        fun bind(theme: Theme) {
            name.text = theme.name
        }
    }

    inner class ConspectusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        internal var name: TextView = itemView.findViewById(R.id.conspectus_name_textview)
        internal var time: TextView = itemView.findViewById(R.id.conspectus_time_textview)
        internal var previewUrl: ImageView = itemView.findViewById(R.id.conspectus_preview_image_view)


        fun bind(conspectus: Conspectus) {


            name.text = conspectus.name
            //time.text = conspectus.time.toString()
            val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault() )

            // Creating date from milliseconds
            // using Date() constructor
            val result: Date
            if (conspectus.time!=null) {
                result = Date(conspectus.time as Long)
                time.text = simple.format(result)
            }else{
                time.text = conspectus.time.toString()
            }

            //Log.d("myLog", conspectus.video_url)

                Picasso.with(itemView.context)

                    .load(conspectus.previewUrl)
                    .placeholder(R.mipmap.user_profile_placeholder)
                    .into(previewUrl)

        }

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition] as BaseItem)
        }
    }

    inner class PageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        internal var name: TextView = itemView.findViewById(R.id.page_name_textview)


        fun bind(page: Page) {
            name.text = page.name
        }

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition] as BaseItem)
        }

    }
    inner class VideoItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private val thumbnail = itemView.findViewById<View>(R.id.video_thumbnail) as ImageView
        private val title = itemView.findViewById<View>(R.id.video_title) as TextView
        private val description = itemView.findViewById<View>(R.id.video_description) as TextView



        fun bind(videoItem: VideoItem) {
            Picasso.with(itemView.context).load(videoItem.thumbnailURL).into(thumbnail)
            title.text = videoItem.title
            description.text = videoItem.description
        }

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition] as VideoItem)
        }

    }
}
