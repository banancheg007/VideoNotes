package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    private var isChangePasswordViewsVisible: MutableLiveData<Boolean>? = null

    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        when(view!!.id){
            R.id.button_logout->{
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                navigationEvent.sendEvent {  val intent = Intent(getApplication(), LoginActivity::class.java)
                    navigationEvent.sendEvent{ startActivity(intent)} }
            }
            R.id.button_change_password->{
                Log.d("myTag", " Change password button clicked")
                isChangePasswordViewsVisible?.value = true
            }
            R.id.button_save_password ->{
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

    fun isChangePasswordViewsVisible(): LiveData<Boolean> {
        if (isChangePasswordViewsVisible == null) {
            isChangePasswordViewsVisible = MutableLiveData()
        }
        return isChangePasswordViewsVisible as MutableLiveData<Boolean>
    }



    private fun loadCurrentUser() {
        currentUser?.value = FirebaseAuth.getInstance().currentUser
    }

    fun setNewPassword(newPassword: String, retypeNewPassword: String) {
        if (newPassword.isEmpty() || retypeNewPassword.isEmpty()){
            Toast.makeText(getApplication(), "Please, fill all fields", Toast.LENGTH_SHORT)
        }else if(newPassword != retypeNewPassword){
            Toast.makeText(getApplication(), "Password and password confirmation do not match", Toast.LENGTH_SHORT ).show()
        }else if(newPassword.length<6 || retypeNewPassword.length<6){
            Toast.makeText(getApplication(), "The password must contain at least 6 characters", Toast.LENGTH_SHORT ).show()
        }else{
            val user = FirebaseAuth.getInstance().currentUser
            user?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("myTag", "User password updated.")
                        isChangePasswordViewsVisible?.value = false
                    }
                }
        }
    }


}