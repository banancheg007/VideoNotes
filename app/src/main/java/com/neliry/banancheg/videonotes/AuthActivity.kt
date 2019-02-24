package com.neliry.banancheg.videonotes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.*
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.viewmodels.ThemesViewModel
import java.security.MessageDigest


class AuthActivity : AppCompatActivity(), View.OnClickListener {





    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    val TAG: String = "myTag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        auth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("225628396953-2vpgba6lr7obg85vp0l331ma6mkoshh1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create()




        button_login.setOnClickListener(this)
        button_logout.setOnClickListener(this)
       sign_in_button.setOnClickListener(this)



        var info: PackageInfo = getPackageManager().getPackageInfo("com.neliry.banancheg.videonotes",
            PackageManager.GET_SIGNATURES);
        for (signature in info.signatures) {
            var md : MessageDigest  = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

        }
        recycler_view.layoutManager = LinearLayoutManager(this)

        login_button_facebook.setReadPermissions("email", "public_profile")
        login_button_facebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                  // ...
            }
        })

        val viewModel = ViewModelProviders.of(this).get(ThemesViewModel::class.java!!)
        viewModel.getItems().observe(this, object : Observer<List<Theme>> {
            override fun onChanged(themes: List<Theme>?) {
                Log.d(TAG, "ON CHANGED")
               for (all in themes!!){
                    Log.d(TAG, " " + all.name)
                }
                recycler_view.adapter = (FirebaseAdapter(themes!!))

            }
        })


    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_login-> {
                if(!editText_email.text.toString().isEmpty() && !editText_password.text.toString().isEmpty()){
                    auth.signInWithEmailAndPassword(editText_email.text.toString(), editText_password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                Toast.makeText(this, "signInWithEmail:success", Toast.LENGTH_SHORT).show()
                                val user = auth.currentUser

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                    // ...
                }
            }
            R.id.button_logout -> {
                auth.signOut()
                LoginManager.getInstance().logOut();
                Log.d(TAG, "logout")
            }

            R.id.sign_in_button ->{
                val signInIntent = mGoogleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, RC_SIGN_IN)
             }
            // R.id.button_register ->
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //Log.d(TAG, currentUser!!.email)
        Log.d(TAG,currentUser.toString())
        updateUI(currentUser)

        val database = FirebaseDatabase.getInstance()
       val myRef = database.getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("themes")
        /*var key = myRef.push().key!!
        var theme = Theme(key, "jnajwdknwakjdn")
        myRef.child(key).setValue(theme)*/

        /*myRef.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                 //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                 //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var theme: Theme? = p0.getValue(Theme::class.java)
               Log.d(TAG, theme.toString())
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                 //To change body of created functions use File | Settings | File Templates.
            }


        })*/

    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            Toast.makeText(
                baseContext, "You are in system",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            return
        }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

                // ...
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithFACEBOOKCredential:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithFACEBOOKCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

}
