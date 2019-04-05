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
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
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
                Log.d("myTag", " Provider - " +  FirebaseAuth.getInstance().currentUser?.providers + FirebaseAuth.getInstance().currentUser?.providers?.size)
                if(FirebaseAuth.getInstance().currentUser?.providers!![0]  == "password" && FirebaseAuth.getInstance().currentUser?.providers?.size == 1) {
                    isChangePasswordViewsVisible?.value = true
                }else{
                    Toast.makeText(getApplication(), "Only users registered by e-mail and password can change the password", Toast.LENGTH_SHORT ).show()
                }
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
        Log.d("myTag","GOOGLE  " + GoogleSignIn.getLastSignedInAccount(getApplication()))
        //Log.d("myTag","FACEBOOK " +  FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().toString()))

    }

    fun setNewPassword(oldPassword: String, newPassword: String, retypeNewPassword: String) {
        if (newPassword.isEmpty() || retypeNewPassword.isEmpty()||oldPassword.isEmpty()){
            Toast.makeText(getApplication(), "Please, fill all fields", Toast.LENGTH_SHORT).show()
        }else if(newPassword != retypeNewPassword){
            Toast.makeText(getApplication(), "Password and password confirmation do not match", Toast.LENGTH_SHORT ).show()
        }else if(newPassword.length<6 || retypeNewPassword.length<6){
            Toast.makeText(getApplication(), "The password must contain at least 6 characters", Toast.LENGTH_SHORT ).show()
        }else{
            val user = FirebaseAuth.getInstance().currentUser
            val credential: AuthCredential= EmailAuthProvider.getCredential(user?.email!!, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener {task ->
                if (task.isSuccessful){
                    user?.updatePassword(newPassword)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("myTag", "User password updated.")
                                isChangePasswordViewsVisible?.value = false
                                Toast.makeText(getApplication(), "Password was changed", Toast.LENGTH_SHORT ).show()
                            }
                        }

                }else{
                    Toast.makeText(getApplication(), "You entered the current password incorrectly", Toast.LENGTH_SHORT ).show()
                }
            }

        }
    }


}