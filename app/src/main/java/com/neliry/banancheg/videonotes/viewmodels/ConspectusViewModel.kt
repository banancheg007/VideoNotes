package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.activities.YoutubeVideoActivity
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.ConspectusRepository
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import java.lang.Exception

class ConspectusViewModel(application: Application):BaseNavigationDrawerViewModel(application), OnViewClickListener {
    val myAdapter: FirebaseAdapter by lazy { FirebaseAdapter(this) }

    private lateinit var currentClickedConspectus: Conspectus
    private var themeRepository: ThemeRepository
    var themeList: MutableLiveData<List<BaseItem>> = MutableLiveData<List<BaseItem>>()

    init{
        @Suppress("UNCHECKED_CAST")
        repository = ConspectusRepository() as FirebaseDatabaseRepository<BaseItem>
        themeRepository = ThemeRepository()
        themeRepository.setDatabaseReference("themes")
    }
    override fun onViewClicked(view: View?, any: Any?) {
        currentClickedConspectus = any as Conspectus
        navigationEvent.sendEvent {  val intent = Intent(getApplication(), YoutubeVideoActivity::class.java)
            intent.putExtra("currentConspectus", currentClickedConspectus)
            navigationEvent.sendEvent{ startActivity(intent)} }
    }

    fun parseIntent(intent: Intent, supportActionBar: androidx.appcompat.app.ActionBar){
        if (intent.getSerializableExtra("currentTheme") !=null) {
        val theme:Theme = intent.getSerializableExtra("currentTheme") as Theme

            repository.setDatabaseReference("conspectuses", theme.id.toString())
            supportActionBar.title = theme.name
        }
        else {
            repository.setDatabaseReference("all_conspectuses")
            supportActionBar.title = "Conspectuses"
        }
    }

    fun loadAllThemes(){
        themeRepository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Theme> {
            override fun onSuccess(result: List<Theme>) {
                themeList!!.value = result
            }
            override fun onError(e: Exception) {
                themeList!!.value = null
            }
        })
    }

    fun getAllThemes():LiveData<List<BaseItem>>{
        loadAllThemes()
        return themeList
    }

    fun addNewItem(name: String, videoUrl: String, previewUrl: String?, parentId: String) {
        if(name.isEmpty()||videoUrl.isEmpty()){
            Toast.makeText(getApplication(),"Please, fill all fields", Toast.LENGTH_SHORT).show()
        }
        else {
            val conspectus = Conspectus()
            val videoId = Uri.parse(videoUrl).getQueryParameter("v")
            conspectus.name = name
            conspectus.videoUrl = videoId
            if (previewUrl == null){
                conspectus.previewUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
            }else {
                conspectus.previewUrl = previewUrl
            }
            conspectus.time = System.currentTimeMillis()
            repository.saveNewItem(conspectus, parentId)
        }
    }


}