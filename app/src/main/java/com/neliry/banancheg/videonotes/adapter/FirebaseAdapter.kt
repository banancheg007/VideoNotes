package com.neliry.banancheg.videonotes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.R

class FirebaseAdapter(private val list: List<Theme>) : RecyclerView.Adapter<FirebaseAdapter.ArticleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.theme_item, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder?.let{
            it.bind(list[position])}
    }



    override fun getItemCount(): Int {
        return list.size
    }


    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var name: TextView


        init {
            name = itemView.findViewById(R.id.theme_name_textview)

        }

        fun bind(theme: Theme) {
            name.setText(theme.name)
        }
    }
}
