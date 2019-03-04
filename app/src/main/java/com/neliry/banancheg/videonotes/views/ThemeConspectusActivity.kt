package com.neliry.banancheg.videonotes.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.viewmodels.ThemeConspectusViewModel
import kotlinx.android.synthetic.main.activity_theme_conspectus.*

class ThemeConspectusActivity : BaseNavigationDrawerActivity() , View.OnClickListener{

    lateinit var themeConspectusViewModel:ThemeConspectusViewModel

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

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
            Log.d("myTag","user + name:$name + email:$email + photoUrl:$photoUrl + emailVerified: $emailVerified + uid: $uid")
        }
        recycler_view.layoutManager = LinearLayoutManager(this)

        themeConspectusViewModel = ViewModelProviders.of(this).get(ThemeConspectusViewModel::class.java!!)
       themeConspectusViewModel.getThemes().observe(this, object : Observer<List<Theme>> {
            override fun onChanged(themes: List<Theme>?) {
                Log.d("myTag", "ON CHANGED")
                for (all in themes!!){
                    Log.d("myTag", " " + all.name)
                }
                recycler_view.adapter = (FirebaseAdapter(themes!!))

            }
        })

    }

}
