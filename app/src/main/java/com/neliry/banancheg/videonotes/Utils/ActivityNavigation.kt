package com.neliry.banancheg.videonotes.Utils

import android.content.Intent

interface ActivityNavigation {
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}