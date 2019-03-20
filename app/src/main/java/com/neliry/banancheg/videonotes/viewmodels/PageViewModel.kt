package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.PageActivity

class PageViewModel(application: Application):FirebaseViewModel(application), OnViewClickListener {

    //private var currentClickedPage: MutableLiveData<Page>? = null
    lateinit var currentClickedPage: Page
    val navigationEvent = LiveMessageEvent<ViewNavigation>()

    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageRepository() as FirebaseDatabaseRepository<BaseItem>
    }
    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
       // currentClickedPage?.value = baseItem as Page
        currentClickedPage = baseItem as Page
       /* navigationEvent.sendEvent {  val intent = Intent(getApplication(), PageActivity::class.java)
            intent.putExtra("currentConspectus", currentClickedConspectus)
            navigationEvent.sendEvent{ startActivity(intent)} }*/
        Log.d("myTag", "clicked on page")
    }

    fun parseIntent(intent: Intent){
        if (intent.getSerializableExtra("currentConspectus") !=null) {
            val conspectus: Conspectus = intent.getSerializableExtra("currentConspectus") as Conspectus
            repository.setDatabaseReference("pages", conspectus.id.toString())
        }
    }

   /* fun getClickedPage(): LiveData<Page> {
        if (currentClickedPage == null) {
            currentClickedPage = MutableLiveData()
        }
        return currentClickedPage!!
    }*/

}