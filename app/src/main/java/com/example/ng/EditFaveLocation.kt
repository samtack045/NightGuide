package com.example.ng

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ng.databinding.ActivityMainBinding

/**
 * A simple [Fragment] subclass.
 * Use the [editFaveLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFaveLocation : AppCompatActivity(), FaveLocationClickListener {

    private lateinit var binding: ActivityMainBinding
    private val faveLocationViewModel: FaveLocationViewModel by viewModels {
        FaveLocationViewModelFactory((application as ContactApplication).faveLocationRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRecyclerView()
    }

    private fun setRecyclerView()
    {
        val editFaveLocation = this
        faveLocationViewModel.faveLocationItems.observe(this){
            binding.contactListRecyclerView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = FaveLocationAdapter(it as MutableList<FaveLocationItem>, editFaveLocation)
            }
        }
    }

    override fun editFaveLocation(faveLocationItem: FaveLocationItem) {
        NewFaveLocationSheet(faveLocationItem, this).show(supportFragmentManager, "newFaveLocationTag")
    }
}