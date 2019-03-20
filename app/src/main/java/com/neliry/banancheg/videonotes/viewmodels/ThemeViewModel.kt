package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener

class ThemeViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {

    private var currentClickedTheme: MutableLiveData<Theme>? = null

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ThemeRepository() as FirebaseDatabaseRepository<BaseItem>
        repository.setDatabaseReference("themes" )
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        Log.d("myTag", "click on theme_item")
         currentClickedTheme?.value = baseItem as Theme
    }

    fun getClickedTheme(): LiveData<Theme> {
        if (currentClickedTheme == null) {
            currentClickedTheme = MutableLiveData()
        }
        return currentClickedTheme!!
    }

}