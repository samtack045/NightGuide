package com.example.ng

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Mms.Addr
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ng.databinding.FragmentNewContactSheetBinding
import com.example.ng.databinding.FragmentNewMessageSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewMessageSheet(var contactItem: ContactItem?, val sentPI: PendingIntent, val addresses: List<Address>?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewMessageSheetBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        binding.butLeaving.setOnClickListener {
            SmsManager.getDefault().sendTextMessage(contactItem?.num, null, "Leaving now!",
            sentPI, null)
        }
        binding.butArrived.setOnClickListener {
            SmsManager.getDefault().sendTextMessage(contactItem?.num, null, "Arrived home.",
                sentPI, null)
        }

        binding.butLocation.setOnClickListener {
            val address =
                addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            SmsManager.getDefault().sendTextMessage(contactItem?.num, null,
                "I am at $address", sentPI, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewMessageSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}