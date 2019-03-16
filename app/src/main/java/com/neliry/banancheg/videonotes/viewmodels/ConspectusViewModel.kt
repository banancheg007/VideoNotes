package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.view.View
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener

class ConspectusViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {
    init{
        @Suppress("UNCHECKED_CAST")
        repository = ConspectusRepository() as FirebaseDatabaseRepository<BaseItem>
        repository.setDatabaseReference("conspectuses","-LYwPvNpYTMeeYUNkFdh" )
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {

    }

}