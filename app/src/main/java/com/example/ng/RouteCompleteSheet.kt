package com.example.ng

import android.app.PendingIntent
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ng.databinding.FragmentNewDeviationSheetBinding
import com.example.ng.databinding.FragmentRouteCompleteSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RouteCompleteSheet(val sentPI: PendingIntent, val contacts: ArrayList<String>?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRouteCompleteSheetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.butmessageContacts.setOnClickListener {
            contacts?.forEach {
                SmsManager.getDefault().sendTextMessage(it, null, "I have reached the destination", sentPI, null)
            }
            dismiss()
        }
    }
}