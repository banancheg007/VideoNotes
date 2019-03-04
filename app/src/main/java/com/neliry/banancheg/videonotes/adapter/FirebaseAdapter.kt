package com.neliry.banancheg.videonotes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page

class FirebaseAdapter(private val list: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_THEME = 0
        const val TYPE_CONSPECTUS = 1
        //const val TYPE_PAGE = 1
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
            else-> throw NullPointerException()
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
           is ThemeViewHolder-> holder.bind(list[position] as Theme)
            is ConspectusViewHolder -> holder.bind(list[position] as Conspectus)
        }
    }



    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

           if (list[position] is Theme) return TYPE_THEME
        return if (list[position] is Conspectus) TYPE_CONSPECTUS
        else 5

    }

    class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var name: TextView = itemView.findViewById(R.id.theme_name_textview)


        fun bind(theme: Theme) {
            name.text = theme.name
        }
    }

    class ConspectusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var name: TextView = itemView.findViewById(R.id.conspectus_name_textview)


        fun bind(conspectus: Conspectus) {
            name.text = conspectus.name
        }
    }
}
