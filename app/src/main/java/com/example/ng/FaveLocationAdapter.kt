package com.example.ng

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ContactItemCellBinding
import com.example.ng.databinding.FaveLocationItemCellBinding

//import kotlinx.coroutines.flow.internal.NoOpContinuation.context


class FaveLocationAdapter(
    private val faveLocationItems: MutableList<FaveLocationItem>,
    private val clickListener: FaveLocationClickListener,

): RecyclerView.Adapter<FaveLocationViewHolder>() {
    private lateinit var faveLocationViewModel: FaveLocationViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaveLocationViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = FaveLocationItemCellBinding.inflate(from, parent, false)
        return FaveLocationViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = faveLocationItems.size


    override fun onBindViewHolder(holder: FaveLocationViewHolder, position: Int) {
        holder.bindContactItem(faveLocationItems[position])
    }








}