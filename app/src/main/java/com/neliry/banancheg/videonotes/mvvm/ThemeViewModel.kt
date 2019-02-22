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

class ThemeViewModel : ViewModel() {

    var list = ArrayList<Theme>()
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
        repository.databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (child in dataSnapshot.getChildren()) {
                    val data = child.getValue(repository.getEntityClass());
                    list.add(data!!)
                }
                themes!!.value = list
            }


        })


    }
}
