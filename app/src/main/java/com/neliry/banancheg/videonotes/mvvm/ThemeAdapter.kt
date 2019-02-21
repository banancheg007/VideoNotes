package com.neliry.banancheg.videonotes.mvvm

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neliry.banancheg.videonotes.R

class ThemeAdapter(private val list: List<Theme>) : RecyclerView.Adapter<ThemeAdapter.ArticleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.theme_item, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder!!.bind(list[position])
    }


    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var name: TextView


        init {
            name = itemView.findViewById(R.id.theme_name)

        }

        fun bind(theme: Theme) {
            name.setText(theme.name)
        }
    }
}
