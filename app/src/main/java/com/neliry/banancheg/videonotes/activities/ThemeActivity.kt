package com.neliry.banancheg.videonotes.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.entities.BaseItem
import com.neliry.banancheg.videonotes.fragments.NewItemDialogFragment
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_theme.*
import com.neliry.banancheg.videonotes.utils.ViewNavigation


class ThemeActivity : BaseNavigationDrawerActivity() ,  ViewNavigation {

    override fun getMainContentLayout(): Int {
        return com.neliry.banancheg.videonotes.R.layout.activity_theme
    }



    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

        supportActionBar?.title = "Themes"
        val numberOfColumns =  2
        val layoutManager =  GridLayoutManager (this, numberOfColumns)
        recycler_view_themes.layoutManager = layoutManager
        recycler_view_themes.addItemDecoration(ItemDecorator(20))


        baseNavigationDrawerViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)

        baseNavigationDrawerViewModel.showDialog.observe(this, Observer {
                isVisible ->
            val currentDialog = NewItemDialogFragment()
            currentDialog.setViewModel(baseNavigationDrawerViewModel)
            if (isVisible == true) {
            currentDialog.show(supportFragmentManager, "New Item")
        }
        })
        baseNavigationDrawerViewModel.navigationEvent.setEventReceiver(this, this)
        baseNavigationDrawerViewModel.getItems().observe(this,
           Observer<List<BaseItem>> { items ->
               Log.d("myTag", "ON CHANGED")
               recycler_view_themes.adapter = (baseNavigationDrawerViewModel as ThemeViewModel).myAdapter
               (recycler_view_themes.adapter as FirebaseAdapter).setItems(items)
           })
    }

}
