package com.neliry.banancheg.videonotes

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(EditorViewModel::class.java) -> {
            EditorViewModel(application) as T
        }
        else -> throw IllegalArgumentException()
    }
}