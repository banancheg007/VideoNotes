package com.neliry.banancheg.videonotes.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.PageViewModel
import kotlinx.android.synthetic.main.activity_page.*

class PageActivity : BaseNavigationDrawerActivity(), ViewNavigation {

    private lateinit var pageViewModel:PageViewModel

    override fun getMainContentLayout(): Int {
        return R.layout.activity_page
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutManager=  LinearLayoutManager(this)
        recycler_view_pages.layoutManager = layoutManager
        recycler_view_pages.addItemDecoration(ItemDecorator(20))

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        registerCallBack2(pageViewModel)
        pageViewModel.navigationEvent.setEventReceiver(this, this)
        val intent: Intent = intent
        pageViewModel.parseIntent(intent)

        pageViewModel.getItems().observe(this,
            Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")
                recycler_view_pages.adapter = (FirebaseAdapter(pageViewModel,items!!))
            })
    }
}
