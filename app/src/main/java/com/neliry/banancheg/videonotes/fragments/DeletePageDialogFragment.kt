package com.neliry.banancheg.videonotes.fragments

import android.os.Bundle

import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.activities.VideoViewModel
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.DialogFragment
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.viewmodels.EditorViewModel


class DeletePageDialogFragment : DialogFragment(), DialogInterface.OnClickListener{

    private lateinit var editorViewModel: EditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editorViewModel = activity?.run {
            ViewModelProviders.of(this).get(EditorViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val adb = AlertDialog.Builder(activity)
            .setTitle("Delete this page?")
            .setPositiveButton(R.string.action_delete, this)
            .setNegativeButton(R.string.cencel, this)
        return adb.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which){
            Dialog.BUTTON_POSITIVE ->
            {
                editorViewModel.deletePage()
            }
            Dialog.BUTTON_NEGATIVE -> {
              dismiss()
            }
        }
    }
}