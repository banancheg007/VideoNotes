package com.neliry.banancheg.videonotes.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import java.lang.Exception

class ThemeViewModel : ViewModel() {

    private var themes: MutableLiveData<List<Theme>>? = null
    private val repository = ThemeRepository()

    fun getThemes(): LiveData<List<Theme>> {
        if (themes == null) {
            themes = MutableLiveData()
            loadThemes()
        }
        return themes!!
    }



     fun loadThemes() {
         repository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Theme>{
             override fun onSuccess(result: List<Theme>) {
                 themes!!.value = result
             }

             override fun onError(e: Exception) {
                 themes!!.value = null
             }

         })


    }
}
