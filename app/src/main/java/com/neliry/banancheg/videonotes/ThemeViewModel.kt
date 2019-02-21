package com.neliry.banancheg.videonotes

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.neliry.banancheg.videonotes.model.Theme
import java.util.ArrayList

public class ThemeViewModel: ViewModel(){
    var themeList: MutableList<Theme> = ArrayList()
     private var themes: MutableLiveData<List<Theme>> = MutableLiveData<List<Theme>>()
    private val repository = ThemeRepository()
    val TAG = "myTag"

    fun getArticles(): LiveData<List<Theme>> {
        if (themes == null) {
            themes = MutableLiveData<List<Theme>>()
            loadThemes()
        }
        return themes
    }

    private fun loadThemes() {
        repository.databaseReference.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var theme: Theme? = p0.getValue(Theme::class.java)
                themeList.add(theme!!)
                Log.d(TAG, theme.toString())
                Log.d(TAG, theme.name)
                themes.value = themeList
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}