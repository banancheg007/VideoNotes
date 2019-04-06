package com.neliry.banancheg.videonotes.views

import android.annotation.SuppressLint
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
import android.widget.Spinner
import androidx.lifecycle.Observer
import com.neliry.banancheg.videonotes.adapter.FireBaseCustomSpinnerAdapter
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.viewmodels.BaseNavigationDrawerViewModel
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel


class DialogNewItem: DialogFragment(),View.OnClickListener{
    private lateinit var viewModel:BaseNavigationDrawerViewModel
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.dialog_button_confirm ->{
                if (viewModel is ConspectusViewModel){
                    viewModel.addNewItem(dialog_editText_name.text.toString(), "conspectuses")
                    viewModel.addNewItem(dialog_editText_name.text.toString(), "all_conspectuses")
                    dismiss()
                }else {
                    viewModel.addNewItem(dialog_editText_name.text.toString(), "themes")
                    dismiss()
                }
            }
            R.id.dialog_button_cancel-> {
            dismiss()}
        }
    }


    fun setViewModel(baseNavigationDrawerViewModel: BaseNavigationDrawerViewModel){
        this.viewModel = baseNavigationDrawerViewModel
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setTitle("New item")
        val dialogWindow = inflater.inflate(R.layout.new_item_dialog, null)
        (dialogWindow.findViewById(R.id.dialog_button_cancel) as Button).setOnClickListener(this)
        (dialogWindow.findViewById(R.id.dialog_button_confirm) as Button).setOnClickListener(this)
        return dialogWindow
    }



    override fun onStart() {
        super.onStart()

        val dialog = dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (viewModel is ConspectusViewModel){
            dialog_linear_layout_with_youtube_search_views.visibility = View.VISIBLE
            (viewModel as ConspectusViewModel).themeList.observe(this , Observer {
                    themes->
                val adapter = FireBaseCustomSpinnerAdapter(activity?.baseContext, android.R.layout.simple_spinner_item,themes )
                //val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                val spinner = view?.findViewById<Spinner>(R.id.spinner)
                spinner?.adapter = adapter
                // заголовок
                spinner?.prompt = "Choose parent theme"
                val currentItem= spinner?.selectedItem as BaseItem
                Log.d("myTag", currentItem.name)

            })
        }
    }
}