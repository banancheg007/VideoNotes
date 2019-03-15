package com.neliry.banancheg.videonotes.viewmodels

import android.view.View

interface OnViewFocusChangeListener{
    fun onChangeFocus(view: View, hasFocus: Boolean, any: Any)
}