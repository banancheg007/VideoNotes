package com.neliry.banancheg.videonotes.views


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.utils.ActivityNavigation
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel

import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity(), View.OnClickListener, ActivityNavigation {

    private lateinit var callback: OnViewClickListener
    private var loginViewModel: LoginViewModel? = null

    private val tag: String = "myTag"

    private fun registerCallBack(callback: OnViewClickListener) {
        this.callback = callback
    }

    override fun onClick(view: View?) {
        callback.onViewClicked(view)
        when(view?.id){
            R.id.button_login-> {
                if(!editText_your_email.text.toString().isEmpty() && !editText_your_password.text.toString().isEmpty()){
                    loginViewModel!!.emailPasswordSignIn(editText_your_email.text.toString(),editText_your_password.text.toString())
                }
            }
            R.id.sign_up_button->{
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
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


        subscribeUi()
    }

    private fun subscribeUi() {
        loginViewModel!!.addGoogleSignInClient()
        loginViewModel!!.startActivityForResultEvent.setEventReceiver(this, this)


    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel!!.onResultFromActivity(requestCode,resultCode,data)

    }

    override fun onStart() {
        super.onStart()
       loginViewModel!!.getCurrentUser().observe(this,
           Observer<FirebaseUser> { currentUser ->
               if (currentUser!= null){
                   Log.d(tag, " current user: " + currentUser.email)
                   val intent = Intent(this@LoginActivity, ThemeConspectusActivity::class.java)
                   startActivity(intent)
               }
           })
    }
}
