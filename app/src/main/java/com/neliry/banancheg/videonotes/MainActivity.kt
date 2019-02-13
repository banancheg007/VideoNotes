package com.neliry.banancheg.videonotes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase




class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var auth: FirebaseAuth
    val TAG: String = "myTag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance();


        button_login.setOnClickListener(this)
        button_logout.setOnClickListener(this)
       // sign_in_button.setOnClickListener(this)


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

            /* R.id.sign_in_button ->{
                 val signInIntent = mGoogleSignInClient.getSignInIntent()
                 startActivityForResult(signInIntent, RC_SIGN_IN)
             }*/
            // R.id.button_register ->
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Log.d(TAG, currentUser!!.email)
        updateUI(currentUser)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            Toast.makeText(
                baseContext, "You are in system",
                Toast.LENGTH_SHORT
            ).show()
        } else {

        }
    }


}
