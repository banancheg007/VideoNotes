package com.neliry.banancheg.videonotes.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button


class SignInFragment(): Fragment(), View.OnClickListener{
    override fun onClick(v: View?) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(com.neliry.banancheg.videonotes.R.id.content, SignUpFragment())
        transaction.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(com.neliry.banancheg.videonotes.R.layout.fragment_sign_in, container, false)
        Log.d("myTag","In on create view of fragment")
        var btnSignUp = view.findViewById(com.neliry.banancheg.videonotes.R.id.sign_up_button) as Button
        btnSignUp.setOnClickListener(this)
        return view
    }

    interface OnMessageClickListener {
        fun onMessageClicked(view: View)
    }
}