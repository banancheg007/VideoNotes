package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.UserProfileChangeRequest
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.views.ThemeActivity


class SignUpViewModel(application: Application): BaseViewModel(application){
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
   // private var currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val navigationEvent = LiveMessageEvent<ViewNavigation>()
        /* fun getCurrentUser(): LiveData<FirebaseUser> {
        currentUser.value = auth.currentUser
        return currentUser!!
    }*/

    private fun getCurrentUser(){
        if (auth.currentUser != null)
            navigationEvent.sendEvent {  val intent = Intent(getApplication(), ThemeActivity::class.java)
                navigationEvent.sendEvent{ startActivity(intent)} }
    }

    fun createUserWithEmailAndPassword(email:String,userName: String, password:String, retypePassword:String){

        if (email.isEmpty() || password.isEmpty() || retypePassword.isEmpty() || userName.isEmpty()){
            Toast.makeText(getApplication(), "Please, fill all fields", Toast.LENGTH_SHORT ).show()
        }else if (!isValidEmail(email)){
            Toast.makeText(getApplication(), "bad email format", Toast.LENGTH_SHORT ).show()
        }else if (password != retypePassword){
           Toast.makeText(getApplication(), "Password and password confirmation do not match", Toast.LENGTH_SHORT ).show()
        }else if (password.length<6 || retypePassword.length<6){
            Toast.makeText(getApplication(), "The password must contain at least 6 characters", Toast.LENGTH_SHORT ).show()
        }else{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("myTag", "createUserWithEmail:success")
                        val user = FirebaseAuth.getInstance().currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(userName)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { //task ->
                                if (task.isSuccessful) {
                                    Log.d("myTag", "User profile updated.")
                                    // currentUser.value = user
                                    getCurrentUser()
                                }
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("myTag", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(getApplication(),"Something went wrong or you have lost your internet connection",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
