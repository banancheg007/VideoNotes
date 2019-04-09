package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation

class PageViewModel(application: Application):BaseNavigationDrawerViewModel(application), OnViewClickListener {

    private lateinit var currentClickedPage: Page

    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageRepository() as FirebaseDatabaseRepository<BaseItem>
    }
    override fun onViewClicked(view: View?, any: Any?) {
        currentClickedPage = any as Page
        Log.d("myTag", "clicked on page")
    }

    fun parseIntent(intent: Intent){
        if (intent.getSerializableExtra("currentConspectus") !=null) {
            val conspectus: Conspectus = intent.getSerializableExtra("currentConspectus") as Conspectus
            repository.setDatabaseReference("pages", conspectus.id.toString())
        }
    }

}