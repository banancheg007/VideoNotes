package com.neliry.banancheg.videonotes.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.neliry.banancheg.videonotes.R
import kotlinx.android.synthetic.main.activity_theme_conspectus.*

class ThemeConspectusActivity : AppCompatActivity() , View.OnClickListener{
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_logout->{
                FirebaseAuth.getInstance().signOut();

                LoginManager.getInstance().logOut();
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_conspectus)
        button_logout.setOnClickListener(this)
    }
}
