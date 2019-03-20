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

    private lateinit var callback: OnViewClickListener
    private lateinit var loginViewModel: LoginViewModel

    private fun registerCallBack(callback: OnViewClickListener) {
        this.callback = callback
    }

    override fun onClick(view: View?) {
        callback.onViewClicked(view)
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
        registerCallBack(loginViewModel)
        sign_in_google_button.setOnClickListener(this)
        sign_in_facebook_button.setOnClickListener(this)
        button_login.setOnClickListener(this)
        sign_up_button.setOnClickListener(this)

        //auth = FirebaseAuth.getInstance();
        // Configure Google Sign In


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
       /*loginViewModel!!.getCurrentUser().observe(this,
           Observer<FirebaseUser> { currentUser ->
               if (currentUser!= null){
                   Log.d(tag, " current user: " + currentUser.email)
                   val intent = Intent(this@LoginActivity, ThemeActivity::class.java)
                   startActivity(intent)
               }
           })*/
    }
}
