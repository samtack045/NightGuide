package com.example.ng

import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ng.databinding.FragmentNewFaveLocationSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [NewFaveLocationSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewFaveLocationSheet(var faveLocationItem: FaveLocationItem?, mapsActivity: AppCompatActivity) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewFaveLocationSheetBinding
    private lateinit var faveLocationViewModel: FaveLocationViewModel
    private val mapsActivity : AppCompatActivity = mapsActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (faveLocationItem != null) {
            val editable = Editable.Factory.getInstance()
            binding.editTextName.text = editable.newEditable(faveLocationItem!!.name)
            binding.editTextLocation.text = editable.newEditable("Location")
        }

        faveLocationViewModel = ViewModelProvider(activity)[FaveLocationViewModel::class.java]
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
        binding = FragmentNewFaveLocationSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun backAction() {
        dismiss()
    }

    fun saveAction() {
        val name = binding.editTextName.text.toString()
        val location = binding.editTextLocation.text.toString()
        val geocode = Geocoder(mapsActivity, Locale.getDefault())
        val addList = geocode.getFromLocationName(location, 1)
        val latitude = addList?.get(0)?.latitude!!
        val longitude = addList[0]?.longitude!!
        if (faveLocationItem == null) {
            val newFaveLocation = FaveLocationItem(name,latitude,longitude)
            faveLocationViewModel.addFaveLocationItem(newFaveLocation)
        } else {
            faveLocationItem!!.name = name
            faveLocationItem!!.latitude = latitude
            faveLocationItem!!.longitude = longitude
            faveLocationViewModel.updateFaveLocationItem(faveLocationItem!!)
        }
        binding.editTextName.setText("")
        binding.editTextLocation.setText("")
        dismiss()
    }
}