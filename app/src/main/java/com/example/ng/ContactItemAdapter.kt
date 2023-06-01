package com.example.ng

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ContactItemCellBinding

class ContactItemAdapter(
    private val contactItems: List<ContactItem>,
    private val clickListener: ContactItemClickListener
): RecyclerView.Adapter<ContactItemViewHolder>() {
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