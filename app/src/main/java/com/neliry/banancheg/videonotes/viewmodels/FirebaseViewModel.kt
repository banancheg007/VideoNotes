package com.neliry.banancheg.videonotes.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import java.lang.Exception

abstract class FirebaseViewModel<Model> : ViewModel() {

    private var themes: MutableLiveData<List<Model>>? = null
    abstract var repository: FirebaseDatabaseRepository<Model>

    fun getItems(): LiveData<List<Model>> {
        if (themes == null) {
            themes = MutableLiveData()
            loadItems()
        }
        return themes!!
    }



    fun loadItems() {
        repository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>{
            override fun onSuccess(result: List<Model>) {
                themes!!.value = result
            }

            override fun onError(e: Exception) {
                themes!!.value = null
            }

        })


    }
}