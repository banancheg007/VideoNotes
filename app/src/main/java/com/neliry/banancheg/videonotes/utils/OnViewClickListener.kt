package com.neliry.banancheg.videonotes.utils

import android.view.View
import com.neliry.banancheg.videonotes.models.BaseItem

interface OnViewClickListener{
    fun onViewClicked(view: View?, baseItem: BaseItem? = null)
}