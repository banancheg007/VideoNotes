package com.neliry.banancheg.videonotes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.entities.BaseItem
import com.neliry.banancheg.videonotes.entities.VideoItem
import com.neliry.banancheg.videonotes.fragments.NewItemDialogFragment
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel
import kotlinx.android.synthetic.main.activity_conspectus.*

class ConspectusActivity : BaseNavigationDrawerActivity(), ViewNavigation {



    override fun getMainContentLayout(): Int {
        return R.layout.activity_conspectus
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutManager=  LinearLayoutManager(this)
        recycler_view_conspectuses.layoutManager = layoutManager
        recycler_view_conspectuses.addItemDecoration(ItemDecorator(20))

        baseNavigationDrawerViewModel = ViewModelProviders.of(this).get(ConspectusViewModel::class.java)


        val intent: Intent = intent
        (baseNavigationDrawerViewModel as ConspectusViewModel).parseIntent(intent, supportActionBar!!)
        baseNavigationDrawerViewModel.navigationEvent.setEventReceiver(this, this)

        baseNavigationDrawerViewModel.getItems().observe(this, Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")
            recycler_view_conspectuses.adapter = (baseNavigationDrawerViewModel as ConspectusViewModel).myAdapter
            (recycler_view_conspectuses.adapter as FirebaseAdapter).setItems(items)
            })

        baseNavigationDrawerViewModel.showDialog.observe(this, Observer {

                isVisible ->
            val currentDialog = NewItemDialogFragment()
            currentDialog.setViewModel(baseNavigationDrawerViewModel)
            if (isVisible == true) {
                currentDialog.show(supportFragmentManager, "New Item")


            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        val videoItem = data.getSerializableExtra("VIDEO_ITEM") as VideoItem
        (supportFragmentManager.findFragmentByTag("New Item") as NewItemDialogFragment).setSelectedVideo(videoItem)

        Log.d("myTag", "video id " + videoItem.id)
    }
}
