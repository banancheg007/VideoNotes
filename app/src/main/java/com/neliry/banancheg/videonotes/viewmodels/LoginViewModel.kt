package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(application: Application): AndroidViewModel(application){
    private lateinit var auth: FirebaseAuth
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("225628396953-2vpgba6lr7obg85vp0l331ma6mkoshh1.apps.googleusercontent.com")
        .requestEmail()
        .build()
    private  var mGoogleSignInClient: GoogleSignInClient= GoogleSignIn.getClient(application.baseContext, gso);
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()


    companion object {
        private const val RC_SIGN_IN = 9001
    }

    val TAG: String = "myTag"
}