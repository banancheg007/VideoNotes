package com.neliry.banancheg.videonotes.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.LoginViewModel
import com.neliry.banancheg.videonotes.viewmodels.UserProfileViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_conspectus.*
import kotlinx.android.synthetic.main.activity_user_profile.*



class UserProfileActivity : AppCompatActivity(), View.OnClickListener, ViewNavigation {

    private lateinit var callback: OnViewClickListener
    private lateinit var userProfileViewModel: UserProfileViewModel

    private fun registerCallBack(callback: OnViewClickListener) {
        this.callback = callback
    }

    override fun onClick(view: View?) {
        callback.onViewClicked(view)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.neliry.banancheg.videonotes.R.layout.activity_user_profile)
        button_logout.setOnClickListener(this)
        button_change_password.setOnClickListener(this)
        button_save_password.setOnClickListener(this)

        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        userProfileViewModel.navigationEvent.setEventReceiver(this, this)
        registerCallBack(userProfileViewModel)

        userProfileViewModel.getCurrentUser().observe(this,
            Observer<FirebaseUser>{ currentUser ->
                Log.d("myTag", "ON CHANGED")
                textView_user_display_name.text = currentUser?.displayName
                textView_user_email.text = currentUser?.email
                Picasso.with(this)
                    .load(currentUser?.photoUrl)
                    .placeholder(R.mipmap.user_profile_placeholder)
                    .into(imageView_user_photo)
            })

        userProfileViewModel.isChangePasswordViewsVisible().observe(this ,Observer<Boolean>{ isChangePasswordViewsVisible ->
            Log.d("myTag", "ON CHANGED")
            if(isChangePasswordViewsVisible == true){
                editText_new_password.visibility = View.VISIBLE
                editText_retype_new_password.visibility = View.VISIBLE
                button_save_password.visibility = View.VISIBLE
            }else{
                editText_new_password.visibility = View.INVISIBLE
                editText_retype_new_password.visibility = View.INVISIBLE
                button_save_password.visibility = View.INVISIBLE
            }
        })
        /*if (user != null) {
            // Name, email address, and profile photo Url
            textView_user_display_name.text = user?.displayName
            textView_user_email.text = user?.email
            Picasso.with(this)
                .load(user?.photoUrl)
                .placeholder(R.mipmap.user_profile_placeholder)
                .into(imageView_user_photo)


            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.

        }*/

    }
}
