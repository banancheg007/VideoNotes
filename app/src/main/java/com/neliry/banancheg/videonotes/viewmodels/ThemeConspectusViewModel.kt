package com.neliry.banancheg.videonotes.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import java.lang.Exception

class ThemeConspectusViewModel:ViewModel(), OnViewFocusChangeListener, OnViewClickListener{


    lateinit var firebaseAdapter: FirebaseAdapter

    override fun onChangeFocus(view: View, hasFocus: Boolean) {
       if(view.hasFocus()){
           view.setBackgroundResource(R.drawable.active_border)
           Log.d("myTag","has focus")
       }else{
           view.setBackgroundResource(R.drawable.border)
           Log.d("myTag","lost focus")
       }
    }

    override fun onButtonClicked(view: View) {
        Log.d("myTag", "On Theme click")
        view.isSelected = true
    }

    val themeRepository: ThemeRepository = ThemeRepository()
    val conspectusRepository: ConspectusRepository = ConspectusRepository()

    private var themes: MutableLiveData<List<Theme>>? = null
    private var conspectuses: MutableLiveData<List<Conspectus>>? = null

    fun getThemes(): LiveData<List<Theme>> {
        if (themes == null) {
            themes = MutableLiveData()
            loadThemes()
        }
        return themes!!
    }
    fun getConspectuses(): LiveData<List<Conspectus>> {
        if (conspectuses == null) {
            conspectuses = MutableLiveData()
            loadConspectuses()
        }
        return conspectuses!!
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

    fun loadConspectuses() {
        conspectusRepository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Conspectus>{
            override fun onSuccess(result: List<Conspectus>) {
               conspectuses!!.value = result
            }

            override fun onError(e: Exception) {
                conspectuses!!.value = null
            }

        })


    }
}