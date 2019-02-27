package com.neliry.banancheg.videonotes.views


import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
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
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel

import com.neliry.banancheg.videonotes.viewmodels.OnButtonClickListener
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity(), View.OnClickListener, ActivityNavigation {

    lateinit var callback: OnButtonClickListener
    private var loginViewModel: LoginViewModel? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

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
                    auth.signInWithEmailAndPassword(editText_your_email.text.toString(), editText_your_password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                Toast.makeText(this, "signInWithEmail:success", Toast.LENGTH_SHORT).show()
                                val user = auth.currentUser

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                    // ...
                }
            }
            /*R.id.button_logout -> {
                auth.signOut()
                LoginManager.getInstance().logOut();
                Log.d(TAG, "logout")
            }*/

            R.id.sign_in_google_button ->{
               // val signInIntent = mGoogleSignInClient.getSignInIntent()
                //startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            // R.id.button_register ->
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

        auth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("225628396953-2vpgba6lr7obg85vp0l331ma6mkoshh1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create()
        loginViewModel!!.addGoogleSignInClient(mGoogleSignInClient)

        sign_in_facebook_button.setReadPermissions("email", "public_profile")
        sign_in_facebook_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })

        subscribeUi()
    }

    private fun subscribeUi() {
        loginViewModel!!.startActivityForResultEvent.setEventReceiver(this, this)

        loginViewModel!!.uiState.observe( this, Observer {
            val uiModel = it

            if (uiModel.showError != null && !uiModel.showError.consumed) {
                uiModel.showError.consume()?.let { showLoginFailed(it) }
            }

            if (uiModel.showSuccess != null && !uiModel.showSuccess.consumed) {
                uiModel.showSuccess.consume()?.let { showLoginSuccess(it) }
            }
        }
        )
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel!!.onResultFromActivity(requestCode,resultCode,data)

    }

    private fun showLoginSuccess(@StringRes successString : Int){
        Toast.makeText(this.applicationContext,successString,Toast.LENGTH_SHORT).show()
        //startActivity<HomeActivity>()
        //finish()
    }

    private fun showLoginFailed(@StringRes errorString : Int) {
        //Snackbar.make(binding.container, errorString, Snackbar.LENGTH_SHORT).show()
        //beginDelayedTransition()
    }



    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithFACEBOOKCredential:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithFACEBOOKCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }

    }

   /* public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

                // ...
            }
    }*/

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            Toast.makeText(
                baseContext, "You are in system",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            return
        }
    }
}
