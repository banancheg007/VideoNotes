package com.neliry.banancheg.videonotes.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.new_item_dialog.*
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import com.neliry.banancheg.videonotes.adapter.FireBaseCustomSpinnerAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.models.VideoItem
import com.neliry.banancheg.videonotes.viewmodels.BaseNavigationDrawerViewModel
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel
import java.text.SimpleDateFormat
import java.util.*


class DialogNewItem: DialogFragment(),View.OnClickListener{
    private lateinit var viewModel:BaseNavigationDrawerViewModel
    var currentVideo= VideoItem()
    lateinit var allThemes: List<BaseItem>
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.dialog_button_confirm ->{
                if (viewModel is ConspectusViewModel){

                        (viewModel as ConspectusViewModel).addNewItem(
                            dialog_editText_name.text.toString(),
                            dialog_video_url.text.toString(),
                            currentVideo.thumbnailURL,
                            allThemes[spinner.selectedItemPosition].id!!
                        )
                        dismiss()

                }else {
                    (viewModel as ThemeViewModel).addNewItem(dialog_editText_name.text.toString(), "themes")
                    dismiss()
                }
            }
            R.id.dialog_button_cancel-> {
            dismiss()}
            R.id.dialog_image_button_find_video->{
                val intent = Intent(activity, SearchActivity::class.java)
                activity?.startActivityForResult(intent, 1)}
        }
    }

    fun setViewModel(baseNavigationDrawerViewModel: BaseNavigationDrawerViewModel){
        //this.viewModel = (activity as BaseNavigationDrawerActivity).baseNavigationDrawerViewModel
    }
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setTitle("New item")
        val dialogWindow = inflater.inflate(R.layout.new_item_dialog, null)
        (dialogWindow.findViewById(R.id.dialog_button_cancel) as Button).setOnClickListener(this)
        (dialogWindow.findViewById(R.id.dialog_button_confirm) as Button).setOnClickListener(this)
        (dialogWindow.findViewById(R.id.dialog_image_button_find_video) as ImageButton).setOnClickListener(this)
        return dialogWindow
    }


    override fun onStart() {
        super.onStart()

        val dialog = dialog
        getDialog()?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.viewModel = (activity as BaseNavigationDrawerActivity).baseNavigationDrawerViewModel
        if (viewModel is ConspectusViewModel){
            dialog_linear_layout_with_youtube_search_views.visibility = View.VISIBLE
            spinner.visibility= View.VISIBLE
            (viewModel as ConspectusViewModel).getAllThemes().observe(this , Observer {
                    themes->
                allThemes = themes
                val adapter = FireBaseCustomSpinnerAdapter(activity?.baseContext, android.R.layout.simple_spinner_item,themes )
                //val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                val spinner = view?.findViewById<Spinner>(R.id.spinner)
                spinner?.adapter = adapter

            })
        }
    }


    fun  setSelectedVideo(videoItem: VideoItem){
        currentVideo = videoItem
        dialog_video_url?.setText("https://www.youtube.com/watch?v="+ videoItem.id)
    }

}