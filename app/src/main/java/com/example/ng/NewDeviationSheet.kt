package com.example.ng

import android.app.PendingIntent
import android.content.DialogInterface
import android.location.Address
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
    val addresses: List<Address>?,
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
        timer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(remaining: Long) {
                binding.countdownText.text = (remaining / 1000).toString()
                if ((remaining / 1000) < 2) {
                    binding.countdownUnits.text = "second"
                }
            }
            override fun onFinish() {
                val address = addresses!![0].getAddressLine(0)
                contacts?.forEach {
                    Log.d("mylog", it)
                    SmsManager.getDefault().sendTextMessage(it, null, "I have gone off route, I am at $address", sentPI, null)
                }
                timer.cancel()
                binding.countdownUnits.text = "seconds"
                timer.start()
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
            val address = addresses!![0].getAddressLine(0)
            contacts?.forEach {
                SmsManager.getDefault().sendTextMessage(it, null, "I have gone off route, I am at $address", sentPI, null)
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

    override fun onDismiss(dialog: DialogInterface) {
        mapsActivity.setARoute(dest)
        mapsActivity.offRoute = false
        super.onDismiss(dialog)
    }
}