package com.neliry.banancheg.videonotes.views



import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sing_up.*

class SignUpActivity : AppCompatActivity(),View.OnClickListener, ViewNavigation {
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onClick(v: View?) {
            signUpViewModel.createUserWithEmailAndPassword(editText_email.text.toString(),editText_username.text.toString(), editText_password.text.toString(), editText_retype_password.text.toString() )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
       button_sign_up.setOnClickListener(this)
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        signUpViewModel.navigationEvent.setEventReceiver(this, this)
       /* signUpViewModel!!.getCurrentUser().observe(this, object : Observer<FirebaseUser> {
            override fun onChanged(currentUser: FirebaseUser?) {
                if (currentUser!= null){
                    Log.d("myTag", " current user: " + currentUser.email)
                    val intent = Intent(this@SignUpActivity, ThemeActivity::class.java)
                    startActivity(intent)
                }
            }
        })*/
    }

}
