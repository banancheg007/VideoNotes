package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.ConspectusActivity

open class ThemeViewModel(application: Application):BaseNavigationDrawerViewModel(application), OnViewClickListener {


    private lateinit var currentClickedTheme: Theme

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ThemeRepository() as FirebaseDatabaseRepository<BaseItem>
        repository.setDatabaseReference("themes" )
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        Log.d("myTag", "click on theme_item")

        currentClickedTheme = baseItem as Theme
        navigationEvent.sendEvent {  val intent = Intent(getApplication(), ConspectusActivity::class.java)
            intent.putExtra("currentTheme", currentClickedTheme)
            intent.putExtra("allThemesList", repository.list)
            navigationEvent.sendEvent{ startActivity(intent)} }
    }

}