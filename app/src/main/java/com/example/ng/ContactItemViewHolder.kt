package com.example.ng

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ContactItemCellBinding

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


        binding.butCall.setOnClickListener{
            clickListener.call(contactItem)
        }

        binding.butMsg.setOnClickListener{
            clickListener.msg(contactItem)
        }


    }




}