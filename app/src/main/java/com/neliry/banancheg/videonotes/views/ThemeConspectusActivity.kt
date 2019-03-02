package com.neliry.banancheg.videonotes.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.neliry.banancheg.videonotes.R
import kotlinx.android.synthetic.main.activity_theme_conspectus.*

class ThemeConspectusActivity : BaseNavigationDrawerActivity() , View.OnClickListener{
    override fun getMainContentLayout(): Int {
        return R.layout.activity_theme_conspectus
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_logout->{
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                val intent = Intent(this@ThemeConspectusActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        button_logout.setOnClickListener(this)
    }

}
