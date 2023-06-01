package com.example.ng

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.ng.databinding.FragmentNewContactSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewContactSheet(var contactItem: ContactItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewContactSheetBinding
    private lateinit var contactViewModel: ContactViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (contactItem != null) {
            binding.contactTitle.text = "Edit contact"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(contactItem!!.name)
            binding.phoneNum.text = editable.newEditable(contactItem!!.num)
        }else{
            binding.contactTitle.text = "New Contact"
        }

        contactViewModel = ViewModelProvider(activity)[ContactViewModel::class.java]
        binding.saveButton.setOnClickListener{
            saveAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewContactSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveAction() {


        val name = binding.name.text.toString()
        val num = binding.phoneNum.text.toString()
        if (contactItem == null) {
            val newContact = ContactItem(name, num)
            contactViewModel.addContactItem(newContact)
        } else{
            contactItem!!.name = name
            contactItem!!.num = num
            contactViewModel.updateContactItem(contactItem!!)
        }
        binding.name.setText("")
        binding.phoneNum.setText("")
        dismiss()

    }
}