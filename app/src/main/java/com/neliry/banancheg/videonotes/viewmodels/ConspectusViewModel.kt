package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.PageActivity

class ConspectusViewModel(application: Application):BaseNavigationDrawerViewModel(application), OnViewClickListener {


    private lateinit var currentClickedConspectus: Conspectus

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ConspectusRepository() as FirebaseDatabaseRepository<BaseItem>
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        currentClickedConspectus = baseItem as Conspectus
        navigationEvent.sendEvent {  val intent = Intent(getApplication(), PageActivity::class.java)
            intent.putExtra("currentConspectus", currentClickedConspectus)
            navigationEvent.sendEvent{ startActivity(intent)} }
    }

    fun parseIntent(intent: Intent){
        if (intent.getSerializableExtra("currentTheme") !=null) {
        val theme:Theme = intent.getSerializableExtra("currentTheme") as Theme
            repository.setDatabaseReference("conspectuses", theme.id.toString())
        }
        else
            repository.setDatabaseReference("all_conspectuses")
    }

}