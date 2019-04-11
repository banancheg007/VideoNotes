package com.neliry.banancheg.videonotes.fragments

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.activities.VideoViewModel
import kotlinx.android.synthetic.main.new_page_dialog_fragment.*

class AddPageDialogFragment : DialogFragment(){


   private lateinit var videoViewModel: VideoViewModel
    private var videoDuration: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoViewModel = activity?.run {
            ViewModelProviders.of(this).get(VideoViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    fun newInstance(title: String, time: Float): AddPageDialogFragment {
        val frag = AddPageDialogFragment()
        val args = Bundle()
        videoDuration = time
        args.putString("title", title)
        frag.arguments = args
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_page_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val defaultTime = videoViewModel.getCurrentTime(videoViewModel.currentTime)
        videoViewModel.isPause = false
        videoViewModel.setPause()
        
        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_ok.setOnClickListener {
            if (videoViewModel.createNewPage(page_name.text.toString(), arrayListOf(oursPicker.value, minutesPiker.value, secondsPiker.value)))
                dismiss()
        }
        oursPicker.minValue = 0
        oursPicker.maxValue = 24
        oursPicker.value = defaultTime[0]

        minutesPiker.minValue = 0
        minutesPiker.maxValue = 59
        minutesPiker.value = defaultTime[1]

        secondsPiker.minValue = 0
        secondsPiker.maxValue = 59
        secondsPiker.value = defaultTime[2]
    }


}