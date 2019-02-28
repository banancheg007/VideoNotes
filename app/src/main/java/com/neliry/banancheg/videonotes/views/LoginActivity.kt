package com.neliry.banancheg.videonotes.views


import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.youtube.player.internal.v
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.Utils.ActivityNavigation
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel

import com.neliry.banancheg.videonotes.viewmodels.OnButtonClickListener
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity(), View.OnClickListener, ActivityNavigation {

    lateinit var callback: OnButtonClickListener
    private var loginViewModel: LoginViewModel? = null
    //private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    companion object {
        private const val RC_SIGN_IN = 9001
    }

    val TAG: String = "myTag"

    fun registerCallBack(callback: OnButtonClickListener) {
        this.callback = callback
    }

    override fun onClick(view: View?) {
        callback.onButtonClicked(view!!)
        when(view!!.id){
            R.id.button_login-> {
                if(!editText_your_email.text.toString().isEmpty() && !editText_your_password.text.toString().isEmpty()){
                    loginViewModel!!.emailPasswordSignIn(editText_your_email.text.toString(),editText_your_password.text.toString())
                }
            }
            R.id.sign_up_button->{
                val intent = Intent(this@LoginActivity, SingUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        registerCallBack(loginViewModel!!)
        sign_in_google_button.setOnClickListener(this)
        sign_in_facebook_button.setOnClickListener(this)
        button_login.setOnClickListener(this)
        sign_up_button.setOnClickListener(this)

        //auth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("225628396953-2vpgba6lr7obg85vp0l331ma6mkoshh1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginViewModel!!.addGoogleSignInClient(mGoogleSignInClient)

        subscribeUi()
    }

    private fun subscribeUi() {
        loginViewModel!!.startActivityForResultEvent.setEventReceiver(this, this)


    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel!!.onResultFromActivity(requestCode,resultCode,data)

    }

    override fun onStart() {
        super.onStart()
       loginViewModel!!.getCurrentUser().observe(this, object : Observer<FirebaseUser> {
            override fun onChanged(currentUser:FirebaseUser?) {
                if (currentUser!= null){
                    Log.d(TAG, " current user: " + currentUser.email)
                    val intent = Intent(this@LoginActivity, ThemeConspectusActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}
