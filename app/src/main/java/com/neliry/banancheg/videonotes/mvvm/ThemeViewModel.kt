package com.neliry.banancheg.videonotes.mvvm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.neliry.banancheg.videonotes.mvvm.repository.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.mvvm.repository.ThemeRepository
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
