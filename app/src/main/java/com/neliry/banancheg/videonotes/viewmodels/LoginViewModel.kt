package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.neliry.banancheg.videonotes.R
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginViewModel(application: Application): AndroidViewModel(application), OnButtonClickListener{
    override fun onButtonClicked(view: View) {
        when(view.id){
            R.id.button_login-> Log.d("myTag", "Button login")
            R.id.sign_in_facebook_button->Log.d("myTag", "Button facebook login")
            R.id.sign_in_google_button->Log.d("myTag", "Button google login")
            R.id.sign_up_button->Log.d("myTag", "Button sign up")
        }
    }

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