package com.example.ghproject.activity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.ghproject.R
import com.example.ghproject.activity.entities.Note
import kotlinx.android.synthetic.main.notes_list_item.view.*

class NotesListAdapter (val windowWidth: Int) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    lateinit var notes: List<Note>

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
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesListAdapter.ViewHolder, position: Int) {
        holder.nameView.text = notes[position].name
    }

    fun setMessages(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }
}