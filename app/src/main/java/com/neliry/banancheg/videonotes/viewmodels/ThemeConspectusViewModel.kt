package com.neliry.banancheg.videonotes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import java.lang.Exception

class ThemeConspectusViewModel:ViewModel(){
    val themeRepository: ThemeRepository = ThemeRepository()
    val conspectusRepository: ConspectusRepository = ConspectusRepository()

    private var themes: MutableLiveData<List<Theme>>? = null

    fun getThemes(): LiveData<List<Theme>> {
        if (themes == null) {
            themes = MutableLiveData()
            loadThemes()
        }
        return themes!!
    }

    fun loadThemes() {
        themeRepository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Theme>{
            override fun onSuccess(result: List<Theme>) {
                themes!!.value = result
            }

            override fun onError(e: Exception) {
                themes!!.value = null
            }

        })


    }
}