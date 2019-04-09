package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.utils.LiveMessageEvent
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.views.SignUpActivity
import com.neliry.banancheg.videonotes.views.ThemeActivity




class LoginViewModel(application: Application): BaseViewModel(application) {

    private lateinit var facebookButton: LoginButton
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val navigationEvent = LiveMessageEvent<ViewNavigation>()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
       const val GOOGLE_SIGN_IN : Int = 9001
    }


    fun getCurrentUser(){
        if (auth.currentUser != null)
            navigationEvent.sendEvent {  val intent = Intent(getApplication(), ThemeActivity::class.java)
                navigationEvent.sendEvent{ startActivity(intent)} }
    }

     fun onViewClicked(view: View?, baseItem: BaseItem? = null) {
        when(view?.id){
            R.id.button_login-> Log.d("myTag", "Button login")
            R.id.sign_in_facebook_button->
            {

                Log.d("myTag", "Button facebook login")
                facebookButton = view as LoginButton
                facebookButton.setReadPermissions("email", "public_profile")
                facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d(tag, "facebook:onSuccess:$loginResult")
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Log.d(tag, "facebook:onCancel")
                        // ...
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(tag, "facebook:onError", error)
                        Toast.makeText(getApplication(),"Something went wrong or you have lost your internet connection",Toast.LENGTH_SHORT).show()
                    }
                })
            }
            R.id.sign_in_google_button->{
                Log.d("myTag", "Button google login")
               googleSignUp()

            }
            R.id.sign_up_button-> {
                Log.d("myTag", "Button sign up")
                val intent = Intent(getApplication(), SignUpActivity::class.java)
                navigationEvent.sendEvent{ startActivity(intent)}
            }
        }
    }
    private fun googleSignUp() {
       val signInIntent = googleSignInClient.signInIntent
       navigationEvent.sendEvent { startActivityForResult(signInIntent, GOOGLE_SIGN_IN) }
   }

    fun addGoogleSignInClient(){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("225628396953-2vpgba6lr7obg85vp0l331ma6mkoshh1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(getApplication<Application>(), gso)

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(tag, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        signIn(authCredential = credential)


    }
    //Called from Activity receving result
    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                googleSignInComplete(task)

            }
        }
    }

    private fun googleSignInComplete(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.apply {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                signIn(authCredential = credential)
            }
        }catch (e: ApiException) {

        }
    }


    fun emailPasswordSignIn(email:String, password:String){
        if (email.isEmpty()||password.isEmpty()){
            Toast.makeText(getApplication(),"Email or password cannot be empty",Toast.LENGTH_SHORT).show()
        }else {
            signIn(email, password)
        }
    }

    private fun signIn(email: String?=null, password: String?=null,authCredential: AuthCredential? = null){
        if(email != null && password != null){
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")
                   // currentUser.value=auth.currentUser
                    getCurrentUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", it.exception)
                    Toast.makeText(getApplication(),"Bad credentials or you have lost your internet connection",Toast.LENGTH_SHORT).show()
                }
                }
        }else{
            auth.signInWithCredential(authCredential!!).addOnCompleteListener{if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                //currentUser.value= auth.currentUser
                getCurrentUser()
                Log.d(tag, "signInWithGoogleCredential:success")
            } else {
                // If sign in fails, display a message to the user.
                Log.w(tag, "signInWithGoogleCredential:failure", it.exception)
                Toast.makeText(getApplication(),"Something went wrong or you have lost your internet connection",Toast.LENGTH_SHORT).show()
            }
            }
        }

    }


    val tag: String = "myTag"
}

