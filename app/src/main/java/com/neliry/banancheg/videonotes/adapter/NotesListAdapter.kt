package com.neliry.banancheg.videonotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.entities.Note
import com.neliry.banancheg.videonotes.models.BaseItem
import kotlinx.android.synthetic.main.notes_list_item.view.*

class NotesListAdapter (val windowWidth: Int) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    lateinit var notesList: List<BaseItem>

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val nameView: TextView = view.findViewById(R.id.notes_name)
            init {
                view.note_relative_layout.layoutParams.width = windowWidth
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
    }

    fun setNotes(notes: List<BaseItem>) {
        this.notesList = notes
        notifyDataSetChanged()
    }
}