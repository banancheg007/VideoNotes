package com.neliry.banancheg.videonotes.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel
import com.neliry.banancheg.videonotes.viewmodels.PageViewModel
import kotlinx.android.synthetic.main.activity_conspectus.*
import kotlinx.android.synthetic.main.activity_page.*

class PageActivity : BaseNavigationDrawerActivity() {

    private lateinit var pageViewModel:PageViewModel

    override fun getMainContentLayout(): Int {
        return R.layout.activity_page
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutManager=  LinearLayoutManager(this)
        recycler_view_pages.layoutManager = layoutManager
        recycler_view_pages.addItemDecoration(ItemDecorator(20))

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java!!)

        val intent: Intent = intent
        pageViewModel.parseIntent(intent)

        pageViewModel.getItems().observe(this,
            Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")
                recycler_view_pages.adapter = (FirebaseAdapter(pageViewModel,items!!))
            })

        pageViewModel.getClickedPage().observe(this,
            Observer<Page> { currentClickedPage ->
                Log.d("myTag", "clicked on conspectus_item")
                if (currentClickedPage != null){
                    Log.d("myTag", "clicked on page")
                }

            })
    }
}
