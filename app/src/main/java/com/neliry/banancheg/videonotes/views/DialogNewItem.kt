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


class DialogNewItem: DialogFragment(),View.OnClickListener{
    private lateinit var themeViewModel:ThemeViewModel
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.dialog_button_confirm ->{
                themeViewModel.addNewItem(dialog_editText_name.text.toString())
                dismiss()
            }
            R.id.dialog_button_cancel-> {
            dismiss()}
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setTitle("New item")
        val dialogWindow = inflater.inflate(R.layout.new_item_dialog, null)
        (dialogWindow.findViewById(R.id.dialog_button_cancel) as Button).setOnClickListener(this)
        (dialogWindow.findViewById(R.id.dialog_button_confirm) as Button).setOnClickListener(this)

        return dialogWindow
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)
    }
}