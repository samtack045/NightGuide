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
    }








}