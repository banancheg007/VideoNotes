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

        setViewModel(baseNavigationDrawerViewModel)
        baseNavigationDrawerViewModel.showDialog.observe(this, Observer {
                isVisible ->
            val currentDialog = DialogNewItem()
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
