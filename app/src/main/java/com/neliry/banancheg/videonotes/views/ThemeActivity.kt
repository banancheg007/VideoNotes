package com.neliry.banancheg.videonotes.views

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_theme.*
import android.content.Intent
import com.neliry.banancheg.videonotes.utils.ViewNavigation


class ThemeActivity : BaseNavigationDrawerActivity() , View.OnClickListener, ViewNavigation {

    private lateinit var callback: OnViewClickListener
    private lateinit var themeViewModel:ThemeViewModel

    private fun registerCallBack(callback: OnViewClickListener) {
        this.callback = callback
    }

    override fun getMainContentLayout(): Int {
        return com.neliry.banancheg.videonotes.R.layout.activity_theme
    }

    override fun onClick(view: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                //button_logout.setOnClickListener(this)


        val numberOfColumns =  1
        val layoutManager =  GridLayoutManager (this, numberOfColumns)
        //val layoutManager=  LinearLayoutManager(this)
        recycler_view_themes.layoutManager = layoutManager
        recycler_view_themes.addItemDecoration(ItemDecorator(20))


        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)
        registerCallBack(themeViewModel)
        themeViewModel.navigationEvent.setEventReceiver(this, this)
       themeViewModel.getItems().observe(this,
           Observer<List<BaseItem>> { items ->
               Log.d("myTag", "ON CHANGED")
               recycler_view_themes.adapter = (FirebaseAdapter(themeViewModel,items))
           })

       /* themeViewModel.getClickedTheme().observe(this,
            Observer<Theme> { currentClickedTheme ->
            Log.d("myTag", "clicked on theme")
                if (currentClickedTheme != null){
                    val intent = Intent(this@ThemeActivity, ConspectusActivity::class.java)
                    intent.putExtra("currentTheme", currentClickedTheme)
                    startActivity(intent)
                }

        })*/

    }

}
