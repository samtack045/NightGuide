package com.example.ng

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ContactItemCellBinding
import com.example.ng.databinding.FragmentNewContactSheetBinding

class ContactItemViewHolder(
    private val context: Context,
    val binding: ContactItemCellBinding,
    private val clickListener: ContactItemClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bindContactItem(contactItem: ContactItem){
        binding.name.text = contactItem.name

        binding.contactCellContainer.setOnClickListener {
            clickListener.editContactItem(contactItem)
        }


    }




}