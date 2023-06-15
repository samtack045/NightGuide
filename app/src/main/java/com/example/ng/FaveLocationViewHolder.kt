package com.example.ng

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ng.databinding.ActivityMapsBinding
import com.example.ng.databinding.ContactItemCellBinding
import com.example.ng.databinding.FaveLocationItemCellBinding

class FaveLocationViewHolder(
    private val context: Context,
    val binding: ActivityMapsBinding,
    private val clickListener: FaveLocationClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bindContactItem(faveLocationItem: FaveLocationItem){
        binding.buEditFave.setOnClickListener {
            clickListener.editFaveLocation(faveLocationItem)
        }

    }




}