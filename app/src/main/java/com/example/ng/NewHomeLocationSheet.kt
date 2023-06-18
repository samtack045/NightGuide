package com.example.ng

import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ng.databinding.FragmentNewHomeLocationSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [NewFaveLocationSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewHomeLocationSheet(var homeItem: HomeItem?, var mapsActivity: AppCompatActivity, var homeLocationDao: HomeLocationDao) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewHomeLocationSheetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            super.onViewCreated(view, savedInstanceState)
            val activity = requireActivity()
            Log.d("myLog", "IN faveLocationSheet")

            if (homeItem != null) {
                Log.d("myLog", "New entry:")

                val editable = Editable.Factory.getInstance()
//                val addresses: List<Address>?
//                val geocoder = Geocoder(mapsActivity, Locale.getDefault())
                Log.d("myLog", homeItem!!.location)

//                addresses = geocoder.getFromLocation(
//                    faveLocationItem!!.latitude,
//                    faveLocationItem!!.longitude,
//                    1
//                )
//                if (addresses == null) {
//                    Log.d("myLog", "NULL")
//                }
//                Log.d("myLog", addresses.toString())
//                val address = addresses!![0].getAddressLine(0)
                binding.editTextLocation.text = editable.newEditable(homeItem!!.location)
            }


            binding.buSave.setOnClickListener {
                saveAction()
            }
            binding.buBack.setOnClickListener {
                backAction()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewHomeLocationSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun backAction() {
        dismiss()
    }

    fun saveAction() {
        lifecycleScope.launch {
        val location = binding.editTextLocation.text.toString()
        val geocode = Geocoder(mapsActivity, Locale.getDefault())
//        val addList = geocode.getFromLocationName(location, 1)
//        val latitude = addList?.get(0)?.latitude!!
//        val longitude = addList[0]?.longitude!!
        if (homeItem == null) {
//            val newFaveLocation = FaveLocationItem(latitude, longitude)
                Log.d("myLog", "inserting")
                homeLocationDao.insertHomeLocation(HomeItem(location))
//            faveLocationViewModel.addFaveLocationItem(newFaveLocation)
            } else {
//                faveLocationItem!!.latitude = latitude
//                faveLocationItem!!.longitude = longitude
                homeItem!!.location = location
                lifecycleScope.launch {
                    homeLocationDao.updateHomeLocation(homeItem!!)
                }
            }
//        binding.editTextLocation.setText("")
            dismiss()
        }
    }
}