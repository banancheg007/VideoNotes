package com.neliry.banancheg.videonotes.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_base_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_base_navigation_drawer.*
import android.content.Intent
import android.widget.Toast
import android.net.Uri
import com.neliry.banancheg.videonotes.utils.OnItemMenuClickListener
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.viewmodels.BaseNavigationDrawerViewModel


abstract class BaseNavigationDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var callback2: OnItemMenuClickListener

    lateinit var baseNavigationDrawerViewModel: BaseNavigationDrawerViewModel

    fun setViewModel(baseNavigationDrawerViewModel: BaseNavigationDrawerViewModel){
        this.baseNavigationDrawerViewModel = baseNavigationDrawerViewModel
    }


     fun registerCallBack2(callback: OnItemMenuClickListener) {
        this.callback2 = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addElements()
    }

    private fun addElements(){
        setContentView(com.neliry.banancheg.videonotes.R.layout.activity_base_navigation_drawer)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, com.neliry.banancheg.videonotes.R.string.navigation_drawer_open, com.neliry.banancheg.videonotes.R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        addMainContentLayout(getMainContentLayout())
    }


    private fun addMainContentLayout(layoutId: Int){
        val layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(layoutId, coordinator)
    }

    abstract fun getMainContentLayout(): Int

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
       callback2.onMenuItemClicked(item)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
