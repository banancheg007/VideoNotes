package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener

class ConspectusViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {

    private var currentClickedConspectus: MutableLiveData<Conspectus>? = null

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ConspectusRepository() as FirebaseDatabaseRepository<BaseItem>
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        currentClickedConspectus?.value = baseItem as Conspectus
    }

    fun parseIntent(intent: Intent){
        if (intent.getSerializableExtra("currentTheme") !=null) {
        val theme:Theme = intent.getSerializableExtra("currentTheme") as Theme
            repository.setDatabaseReference("conspectuses", theme.id.toString())
        }
        else
            repository.setDatabaseReference("conspectuses")
    }

    fun getClickedConspectus(): LiveData<Conspectus> {
        if (currentClickedConspectus == null) {
            currentClickedConspectus = MutableLiveData()
        }
        return currentClickedConspectus!!
    }

}