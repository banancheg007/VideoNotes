package com.neliry.banancheg.videonotes.utils

import android.view.View
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.VideoItem

interface OnViewClickListener{
    fun onViewClicked(view: View?, any: Any? = null)

}