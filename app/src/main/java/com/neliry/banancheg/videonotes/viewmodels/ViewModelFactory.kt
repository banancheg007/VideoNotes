package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(EditorViewModel::class.java) -> {
            EditorViewModel(application) as T
        }
        else -> throw IllegalArgumentException()
    }
}