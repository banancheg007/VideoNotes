package com.neliry.banancheg.videonotes.views

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel

import com.neliry.banancheg.videonotes.viewmodels.OnButtonClickListener
import com.neliry.banancheg.videonotes.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity() : AppCompatActivity(), View.OnClickListener {

    lateinit var callback: OnButtonClickListener
    private var loginViewModel: LoginViewModel? = null

    fun registerCallBack(callback: OnButtonClickListener) {
        this.callback = callback
    }

    override fun onClick(view: View?) {
        callback.onButtonClicked(view!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, ViewModelFactory(application)).get(LoginViewModel::class.java)
        registerCallBack(loginViewModel!!)
        sign_in_google_button.setOnClickListener(this)
        sign_in_facebook_button.setOnClickListener(this)
        button_login.setOnClickListener(this)
        sign_up_button.setOnClickListener(this)
    }


}
