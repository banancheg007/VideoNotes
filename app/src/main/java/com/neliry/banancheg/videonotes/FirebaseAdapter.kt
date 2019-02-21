package com.neliry.banancheg.videonotes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.neliry.banancheg.videonotes.model.Theme
import java.lang.NullPointerException
import java.util.ArrayList

class FirebaseAdapter(list: List<Theme>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var themeList: List<Theme> = ArrayList()

         val TYPE_THEME = 0
    init{
        this.themeList = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_THEME -> {
                return ThemeViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.theme_item, parent, false)
                )
            }
            else -> throw NullPointerException()

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ThemeViewHolder-> {
                holder.bind(themeList[position])
            }

        }
    }




    override fun getItemViewType(position: Int): Int {
       return TYPE_THEME
    }

    override fun getItemCount(): Int {
        return themeList.size

    }



    inner class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val themeNametext: TextView = view.findViewById(R.id.theme_name_textview)
        fun bind(theme: Theme) {
            themeNametext.text = theme.name
        }
    }
    fun setAdapter(messages: List<Theme>) {
        this.themeList = messages as ArrayList<Theme>
        notifyDataSetChanged()
    }
}

