package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.ConspectusActivity
import com.neliry.banancheg.videonotes.views.ThemeActivity

class ThemeViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {

   // private var currentClickedTheme: MutableLiveData<Theme>? = null
    val navigationEvent = LiveMessageEvent<ViewNavigation>()
    lateinit var currentClickedTheme: Theme

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ThemeRepository() as FirebaseDatabaseRepository<BaseItem>
        repository.setDatabaseReference("themes" )
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        Log.d("myTag", "click on theme_item")
         //currentClickedTheme?.value = baseItem as Theme
        currentClickedTheme = baseItem as Theme
        navigationEvent.sendEvent {  val intent = Intent(getApplication(), ConspectusActivity::class.java)
            intent.putExtra("currentTheme", currentClickedTheme)
            navigationEvent.sendEvent{ startActivity(intent)} }
    }

    /*fun getClickedTheme(): LiveData<Theme> {
        if (currentClickedTheme == null) {
            currentClickedTheme = MutableLiveData()
        }
        return currentClickedTheme!!
    }*/

}