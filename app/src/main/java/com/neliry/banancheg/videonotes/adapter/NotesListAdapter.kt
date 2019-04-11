package com.neliry.banancheg.videonotes.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notes_list_item.view.*

class NotesListAdapter (private val onViewClickListener: OnViewClickListener, val windowWidth: Int, val context: Context) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    lateinit var notesList: List<Page>

    inner class ViewHolder internal constructor(pageView: View) : RecyclerView.ViewHolder(pageView), View.OnClickListener {
        internal val nameView: TextView = pageView.findViewById(R.id.notes_name)
        internal val contentView: ImageView = pageView.findViewById(R.id.notes_content)
        internal val timeView: TextView = pageView.findViewById(R.id.notes_time)
        init {
            pageView.note_linear_layout.layoutParams.width = windowWidth
            pageView.note_linear_layout.setOnClickListener(this)
        }
        override fun onClick(view: View) {

            onViewClickListener.onViewClicked(view, notesList[layoutPosition])
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NotesListAdapter.ViewHolder, position: Int) {
        holder.nameView.text = notesList[position].name
        holder.contentView.layoutParams.height = (notesList[position].height!!*(windowWidth/notesList[position].width!!)).toInt()
        holder.contentView.requestLayout()
        Picasso.with(context)
            .load(notesList[position].content)
            .fit()
            .into( holder.contentView)
        holder.timeView.text = notesList[position].creationTime
    }

    fun setNotes(notes: List<Page>) {
        this.notesList = notes
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}