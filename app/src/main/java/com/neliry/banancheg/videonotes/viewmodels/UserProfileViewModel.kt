package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.LoginActivity

class UserProfileViewModel(application: Application): BaseViewModel(application), OnViewClickListener{

    val navigationEvent = LiveMessageEvent<ViewNavigation>()
    private var currentUser: MutableLiveData<FirebaseUser>? = null

    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        when(view!!.id){
            R.id.button_logout->{
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                navigationEvent.sendEvent {  val intent = Intent(getApplication(), LoginActivity::class.java)
                    navigationEvent.sendEvent{ startActivity(intent)} }
            }
            R.id.button_change_password->{

            }
        }
    }

    fun getCurrentUser(): LiveData<FirebaseUser> {
        if (currentUser == null) {
            currentUser = MutableLiveData()
            loadCurrentUser()
        }
        return currentUser!!
    }

    fun loadCurrentUser() {
        currentUser?.value = FirebaseAuth.getInstance().currentUser
    }


}