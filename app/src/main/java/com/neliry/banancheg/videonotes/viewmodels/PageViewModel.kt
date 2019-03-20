package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener

class PageViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {

    private var currentClickedPage: MutableLiveData<Page>? = null

    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageRepository() as FirebaseDatabaseRepository<BaseItem>
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        currentClickedPage?.value = baseItem as Page
    }

    fun parseIntent(intent: Intent){
        if (intent.getSerializableExtra("currentConspectus") !=null) {
            val conspectus: Conspectus = intent.getSerializableExtra("currentConspectus") as Conspectus
            repository.setDatabaseReference("pages", conspectus.id.toString())
        }
    }

    fun getClickedPage(): LiveData<Page> {
        if (currentClickedPage == null) {
            currentClickedPage = MutableLiveData()
        }
        return currentClickedPage!!
    }

}