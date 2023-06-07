package com.example.ng

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ContactItemCellBinding
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context


class ContactItemAdapter(
    private val contactItems: MutableList<ContactItem>,
    private val clickListener: ContactItemClickListener,

): RecyclerView.Adapter<ContactItemViewHolder>() {
    private lateinit var contactViewModel: ContactViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ContactItemCellBinding.inflate(from, parent, false)
        return ContactItemViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = contactItems.size


    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        holder.bindContactItem(contactItems[position])

//        holder.binding.deleteContact.setOnClickListener {
//            val contactItem = contactItems[position]
//
//
//            // Delete the contact item from the list.
//            contactItems.remove(contactItem)
//            //NewContactSheet(null).deleteContact(contactItem)
//            //contactViewModel.deleteContactItem(contactItem)
//
//            // Notify the adapter that the list has changed.
//            this.notifyDataSetChanged()
//
//
//            // Show a toast message to confirm that the contact was deleted.
//            //Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
//        }
    }








}