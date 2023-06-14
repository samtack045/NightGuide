package com.example.ng

import android.app.PendingIntent
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ng.databinding.FragmentNewDeviationSheetBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewDeviationSheet(
    val sentPI: PendingIntent,
    val contacts: ArrayList<String>?,
    val dest: LatLng,
    val mapsActivity: MapsActivity
): BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewDeviationSheetBinding
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        mapsActivity.offRoute = true
        timer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(remaining: Long) {
                binding.countdownText.text = (remaining / 1000).toString()
                if ((remaining / 1000) < 2) {
                    binding.countdownUnits.text = "second"
                }
            }
            override fun onFinish() {
                contacts?.forEach {
                    Log.d("mylog", it)
                    SmsManager.getDefault().sendTextMessage(it, null, "I have gone off route", sentPI, null)
                }
                binding.countdownText.text = ""
                binding.countdownMessage.text = "Your contacts have been messaged"
                binding.butmessageContacts.visibility = View.INVISIBLE
                binding.countdownUnits.visibility = View.INVISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.butReroute.setOnClickListener {
            mapsActivity.setARoute(dest)
            mapsActivity.offRoute = false
            dismiss()
        }

        binding.butmessageContacts.setOnClickListener {
            contacts?.forEach {
                SmsManager.getDefault().sendTextMessage(it, null, "I have gone off route", sentPI, null)
            }
            mapsActivity.setARoute(dest)
            mapsActivity.offRoute = false
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewDeviationSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}