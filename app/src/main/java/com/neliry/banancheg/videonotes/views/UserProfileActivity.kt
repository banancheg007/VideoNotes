package com.neliry.banancheg.videonotes.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.neliry.banancheg.videonotes.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.nav_header_base_navigation_drawer.*


class UserProfileActivity : AppCompatActivity() {
    var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.neliry.banancheg.videonotes.R.layout.activity_user_profile)

        if (user != null) {
            // Name, email address, and profile photo Url
            textView_user_display_name.text = user?.displayName
            textView_user_email.text = user?.getEmail()
            Picasso.with(this)
                .load(user?.photoUrl)
                .placeholder(R.mipmap.user_profile_placeholder)
                .into(imageView_user_photo)


            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.

        }

    }
}
