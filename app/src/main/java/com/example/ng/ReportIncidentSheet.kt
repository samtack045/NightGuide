package com.example.ng

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.ng.databinding.FragmentNewContactSheetBinding
import com.example.ng.databinding.FragmentReportIncidentBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class ReportIncidentSheet(var latLng: LatLng, var mapsActivity: MapsActivity, var ipDao: IPDao) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentReportIncidentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        binding.butReport.setOnClickListener {
            reportAction()
        }
        binding.butCancel.setOnClickListener {
            cancelAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportIncidentBinding.inflate(inflater, container, false)
        return binding.root
    }
//
    fun cancelAction() {
        dismiss()
    }

    private fun reportAction() {
        mapsActivity.getMap().addMarker(MarkerOptions().position(latLng).title("Incident Reported"))
        lifecycleScope.launch{
            ipDao.insertIP(IncidentPoint(latLng.latitude, latLng.longitude))
            Log.d("myLog", "this is the db" + ipDao.allPoints().asLiveData().value.toString())
        }
        dismiss()
    }
}