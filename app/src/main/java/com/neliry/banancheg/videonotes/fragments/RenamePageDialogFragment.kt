package com.neliry.banancheg.videonotes.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel
import android.view.LayoutInflater
import android.widget.EditText


class RenamePageDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var editorViewModel: EditorViewModel
    lateinit var input : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editorViewModel = activity?.run {
            ViewModelProviders.of(this).get(EditorViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        input = EditText(activity)
        val adb = AlertDialog.Builder(activity)
            .setTitle("Rename page")
            .setView(input)
            .setPositiveButton(R.string.action_rename, this)
            .setNegativeButton(R.string.cencel, this)
        return adb.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

        when (which){
            Dialog.BUTTON_POSITIVE ->
            {
                if (input.text.toString() != "")
                        editorViewModel.renamePage(input.text.toString())
            }
            Dialog.BUTTON_NEGATIVE -> {
                dismiss()
            }
        }
    }
}