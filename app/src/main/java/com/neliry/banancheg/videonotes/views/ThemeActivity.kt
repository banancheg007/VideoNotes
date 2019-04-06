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
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_theme.*
import com.neliry.banancheg.videonotes.utils.ViewNavigation


class ThemeActivity : BaseNavigationDrawerActivity() ,  ViewNavigation {

    private lateinit var callback: OnViewClickListener
    private lateinit var themeViewModel:ThemeViewModel

    private fun registerCallBack(callback: OnViewClickListener) {
        this.callback = callback
    }

    override fun getMainContentLayout(): Int {
        return com.neliry.banancheg.videonotes.R.layout.activity_theme
    }





    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)


        val numberOfColumns =  2
        val layoutManager =  GridLayoutManager (this, numberOfColumns)
        recycler_view_themes.layoutManager = layoutManager
        recycler_view_themes.addItemDecoration(ItemDecorator(20))


        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)

        setViewModel(themeViewModel)
        registerCallBack(themeViewModel)
        registerCallBack2(themeViewModel)

        themeViewModel.showDialog.observe(this, Observer {

                isVisible ->
            val currentDialog = DialogNewItem()
            currentDialog.setViewModel(baseNavigationDrawerViewModel)
            if (isVisible == true) {
            currentDialog.show(supportFragmentManager, "New Item")
        }
        })

        themeViewModel.navigationEvent.setEventReceiver(this, this)
       themeViewModel.getItems().observe(this,
           Observer<List<BaseItem>> { items ->
               Log.d("myTag", "ON CHANGED")
               recycler_view_themes.adapter = (FirebaseAdapter(themeViewModel,items))
           })
    }

}
