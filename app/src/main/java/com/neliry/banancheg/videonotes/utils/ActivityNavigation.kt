package com.neliry.banancheg.videonotes.utils

import android.content.Intent

interface ActivityNavigation {
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}