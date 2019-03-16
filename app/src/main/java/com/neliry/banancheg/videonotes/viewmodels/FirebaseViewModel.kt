package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import java.lang.Exception

open class FirebaseViewModel(application: Application): BaseViewModel(application){
    lateinit var repository: FirebaseDatabaseRepository<BaseItem>
    private var items: MutableLiveData<List<BaseItem>>? = null

    fun getItems(): LiveData<List<BaseItem>> {
        if (items == null) {
            items = MutableLiveData()
            loadItems()
        }
        return items!!
    }

    fun loadItems() {
       repository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<BaseItem>{
            override fun onSuccess(result: List<BaseItem>) {
                items!!.value = result
            }

            override fun onError(e: Exception) {
                items!!.value = null
            }

        })


    }
}