package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnItemMenuClickListener
import com.neliry.banancheg.videonotes.utils.SingleLiveEvent
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.ConspectusActivity
import com.neliry.banancheg.videonotes.views.DialogNewItem
import com.neliry.banancheg.videonotes.views.ThemeActivity
import com.neliry.banancheg.videonotes.views.UserProfileActivity
import java.lang.Exception

open class BaseNavigationDrawerViewModel(application: Application): BaseViewModel(application), OnItemMenuClickListener{

    val navigationEvent = LiveMessageEvent<ViewNavigation>()

    val showDialog = SingleLiveEvent<Boolean>()

    override fun onMenuItemClicked(menuItem: MenuItem) {
        when (menuItem.itemId) {
            com.neliry.banancheg.videonotes.R.id.nav_all_conspectuses -> {
                //startActivity(Intent(this, ConspectusActivity::class.java))
                navigationEvent.sendEvent{ startActivity(Intent(getApplication(), ConspectusActivity::class.java))}
                Log.d("myTag", "on item menu clicked")
            }
            com.neliry.banancheg.videonotes.R.id.nav_themes -> {
                //startActivity(Intent(this, ThemeActivity::class.java))
                navigationEvent.sendEvent{ startActivity(Intent(getApplication(), ThemeActivity::class.java))}
                Log.d("myTag", "on item menu clicked")
            }
            com.neliry.banancheg.videonotes.R.id.nav_account -> {
               // startActivity(Intent(this, UserProfileActivity::class.java))
                navigationEvent.sendEvent{ startActivity(Intent(getApplication(), UserProfileActivity::class.java))}
                Log.d("myTag", "on item menu clicked")
            }
            /*com.neliry.banancheg.videonotes.R.id.nav_settings -> {
                // startActivity(Intent(this, UserProfileActivity::class.java))
                Log.d("myTag", "on item menu clicked")
            }*/
            com.neliry.banancheg.videonotes.R.id.nav_share -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = "I use VideoNotes app. Download this app from google play market"
                val shareSub = "VideoNotes"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub)
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                //startActivity(Intent.createChooser(sharingIntent, "Share using"))
                navigationEvent.sendEvent{ startActivity(Intent.createChooser(sharingIntent, "Share using"))}
                Log.d("myTag", "on item menu clicked")
            }
            com.neliry.banancheg.videonotes.R.id.nav_send -> {
                sendEmail()
                Log.d("myTag", "on item menu clicked")
            }
        }
    }

    private fun sendEmail() {

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:" + "littletester007@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "VideoNotes")

        try {
           // startActivity(Intent.createChooser(emailIntent, "Send email using..."))
            navigationEvent.sendEvent{ startActivity(Intent.createChooser(emailIntent, "Send email using..."))}
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(getApplication(), "No email clients installed.", Toast.LENGTH_SHORT).show()
        }

    }

    lateinit var repository: FirebaseDatabaseRepository<BaseItem>
    private var items: MutableLiveData<List<BaseItem>>? = null

    fun getItems(): LiveData<List<BaseItem>> {
        if (items == null) {
            items = MutableLiveData()
            loadItems()
        }
        return items!!
    }

    private fun loadItems() {
       repository.addListener(object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<BaseItem>{
            override fun onSuccess(result: List<BaseItem>) {
                items!!.value = result
            }
            override fun onError(e: Exception) {
                items!!.value = null
            }
        })
    }

    fun showDialog() {
        showDialog.value = true
    }


}