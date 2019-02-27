package com.neliry.banancheg.videonotes.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.Utils.ActivityNavigation
import com.neliry.banancheg.videonotes.Utils.Event
import com.neliry.banancheg.videonotes.Utils.LiveMessageEvent
import kotlinx.android.synthetic.main.activity_login.view.*


const val GOOGLE_SIGN_IN : Int = 9001

class LoginViewModel(application: Application): AndroidViewModel(application), OnButtonClickListener{

    private  var auth: FirebaseAuth = FirebaseAuth.getInstance();
    val startActivityForResultEvent = LiveMessageEvent<ActivityNavigation>()
    lateinit var googleSignInClient: GoogleSignInClient
    private val _uiState = MutableLiveData<LoginUiModel>()
    val uiState: LiveData<LoginUiModel>
        get() = _uiState

    override fun onButtonClicked(view: View) {
        when(view.id){
            R.id.button_login-> Log.d("myTag", "Button login")
            R.id.sign_in_facebook_button->Log.d("myTag", "Button facebook login")
            R.id.sign_in_google_button->{
                Log.d("myTag", "Button google login")
               googleSignUp()
            }
            R.id.sign_up_button->Log.d("myTag", "Button sign up")
        }
    }
    fun googleSignUp() {
       val signInIntent = googleSignInClient.signInIntent
       startActivityForResultEvent.sendEvent { startActivityForResult(signInIntent, GOOGLE_SIGN_IN) }
   }

    fun addGoogleSignInClient(googleSignInClient: GoogleSignInClient){
        this.googleSignInClient = googleSignInClient
    }

    //Called from Activity receving result
    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                googleSignInComplete(task)

            }
            else ->{
                emitUiState(
                    showError = Event(R.string.login_failed)
                )
            }
        }
    }

    private fun googleSignInComplete(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.apply {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                emitUiState(
                    showSuccess = Event(R.string.login_successful)
                )
            }
        }catch (e: ApiException) {
            emitUiState(
                showError = Event(R.string.login_failed)
            )
        }
    }

    private fun emitUiState(
        showSuccess: Event<Int>? = null,
        showError: Event<Int>? = null
    ) {
        val uiModel = LoginUiModel(showSuccess,showError)
        _uiState.value = uiModel
    }



    val TAG: String = "myTag"
}

data class LoginUiModel(
    val showSuccess : Event<Int>?,
    val showError : Event<Int>?
)