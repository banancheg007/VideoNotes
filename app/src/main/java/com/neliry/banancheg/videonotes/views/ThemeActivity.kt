package com.neliry.banancheg.videonotes.views

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_theme.*


class ThemeActivity : BaseNavigationDrawerActivity() , View.OnClickListener{

    private lateinit var themeViewModel:ThemeViewModel

    override fun getMainContentLayout(): Int {
        return R.layout.activity_theme
    }

    override fun onClick(v: View?) {
        when(v!!.id){
           /* R.id.button_logout->{
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                val intent = Intent(this@ThemeActivity, LoginActivity::class.java)
                startActivity(intent)
            }*/
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                //button_logout.setOnClickListener(this)


        val numberOfColumns =  2
        val layoutManager =  GridLayoutManager (this, numberOfColumns)
        //val layoutManager=  LinearLayoutManager(this)
        recycler_view_themes.layoutManager = layoutManager
        recycler_view_themes.addItemDecoration(ItemDecorator(20))


        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java!!)
       themeViewModel.getItems().observe(this,
           Observer<List<BaseItem>> { items ->
               Log.d("myTag", "ON CHANGED")
               recycler_view_themes.adapter = (FirebaseAdapter(themeViewModel,items!!))
           })

    }

}
