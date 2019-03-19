package com.neliry.banancheg.videonotes.utils

import android.content.Intent

interface ViewNavigation {
    fun startActivity(intent: Intent?)
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}