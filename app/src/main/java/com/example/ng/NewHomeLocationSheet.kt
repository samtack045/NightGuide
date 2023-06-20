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
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        Log.d("myLog", "IN faveLocationSheet")

        if (homeItem != null) {
            Log.d("myLog", "New entry:")

            val editable = Editable.Factory.getInstance()
            Log.d("myLog", homeItem!!.location)
            binding.editTextLocation.text = editable.newEditable(homeItem!!.location)
        }


        binding.buSave.setOnClickListener {
            saveAction()
        }
        binding.buBack.setOnClickListener {
            backAction()
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
        val location = binding.editTextLocation.text.toString()
        lifecycleScope.launch {
            if (homeItem == null) {
                Log.d("myLog", "inserting")
                homeLocationDao.insertHomeLocation(HomeItem(location))
            } else {
                val x = homeItem!!
                x.location = location
                //Log.d("myLog", )
                homeLocationDao.updateHomeLocation(x)
            }
        }
        binding.editTextLocation.setText("")
        Log.d("myLog", "dismissing")
        dismiss()
    }
}