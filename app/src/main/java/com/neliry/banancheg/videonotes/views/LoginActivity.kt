package com.neliry.banancheg.videonotes.views


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity(), View.OnClickListener, ViewNavigation {


    private lateinit var loginViewModel: LoginViewModel

    override fun onClick(view: View?) {
        loginViewModel.onViewClicked(view)
        when(view?.id){
            R.id.button_login-> {
                    loginViewModel.emailPasswordSignIn(editText_your_email.text.toString(),editText_your_password.text.toString())

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        sign_in_google_button.setOnClickListener(this)
        sign_in_facebook_button.setOnClickListener(this)
        button_login.setOnClickListener(this)
        sign_up_button.setOnClickListener(this)


        subscribeUi()
    }

    private fun subscribeUi() {
        loginViewModel.addGoogleSignInClient()
        loginViewModel.navigationEvent.setEventReceiver(this, this)
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.onResultFromActivity(requestCode,resultCode,data)

    }

    override fun onStart() {
        super.onStart()
        loginViewModel.getCurrentUser()
    }
}
