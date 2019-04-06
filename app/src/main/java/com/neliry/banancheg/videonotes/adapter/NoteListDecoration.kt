package com.neliry.banancheg.videonotes.activities

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class NoteListDecoration(context: Context): RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        outRect.set(16.px, 6.px, 16.px, 16.px)
    }

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        a.recycle()
    }

    private val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}