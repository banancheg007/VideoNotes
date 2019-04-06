package com.neliry.banancheg.videonotes.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FireBaseCustomSpinnerAdapter
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.utils.ViewNavigation
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel
import kotlinx.android.synthetic.main.activity_conspectus.*
import kotlinx.android.synthetic.main.new_item_dialog.*

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
        (baseNavigationDrawerViewModel as ConspectusViewModel).parseIntent(intent)
        baseNavigationDrawerViewModel.navigationEvent.setEventReceiver(this, this)

        setViewModel(baseNavigationDrawerViewModel)
        baseNavigationDrawerViewModel.getItems().observe(this, Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")
                recycler_view_conspectuses.adapter = (FirebaseAdapter((baseNavigationDrawerViewModel as ConspectusViewModel),items!!))
            })

        baseNavigationDrawerViewModel.showDialog.observe(this, Observer {

                isVisible ->
            val currentDialog = DialogNewItem()
            currentDialog.setViewModel(baseNavigationDrawerViewModel)
            if (isVisible == true) {
                currentDialog.show(supportFragmentManager, "New Item")

                //dialog_linear_layout_with_youtube_search_views.visibility = View.VISIBLE

            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        val videoItem = data.getSerializableExtra("VIDEO_ITEM") as VideoItem
        Log.d("myTag", "video id " + videoItem.id)
    }
}
