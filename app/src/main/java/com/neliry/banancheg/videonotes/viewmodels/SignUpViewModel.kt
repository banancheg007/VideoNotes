package com.neliry.banancheg.videonotes.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest




class SignUpViewModel: ViewModel(){
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance();
    private var currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    fun getCurrentUser(): LiveData<FirebaseUser> {
        currentUser.value = auth.currentUser
        return currentUser!!
    }

    fun createUserWithEmailAndPassword(email:String,userName: String, password:String, retypePassword:String){
        if((password == retypePassword) && !email.isEmpty() && !password.isEmpty() && !retypePassword.isEmpty() && !userName.isEmpty()){
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
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("myTag", "User profile updated.")
                                    currentUser.value = user
                                }
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("myTag", "createUserWithEmail:failure", task.exception)
                    }

                    // ...
                }

        }else return
    }

    fun isValidEmail(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
