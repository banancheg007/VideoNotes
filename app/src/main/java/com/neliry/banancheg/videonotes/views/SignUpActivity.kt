package com.neliry.banancheg.videonotes.views


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel
import com.neliry.banancheg.videonotes.viewmodels.SignUpViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sing_up.*

class SignUpActivity : AppCompatActivity(),View.OnClickListener {
    private var signUpViewModel: SignUpViewModel? = null

    override fun onClick(v: View?) {
            signUpViewModel?.createUserWithEmailAndPassword(editText_email.text.toString(),editText_username.text.toString(), editText_password.text.toString(), editText_retype_password.text.toString() )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
       button_sign_up.setOnClickListener(this)
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        signUpViewModel!!.getCurrentUser().observe(this, object : Observer<FirebaseUser> {
            override fun onChanged(currentUser: FirebaseUser?) {
                if (currentUser!= null){
                    Log.d("myTag", " current user: " + currentUser.email)
                    val intent = Intent(this@SignUpActivity, ThemeConspectusActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

}
