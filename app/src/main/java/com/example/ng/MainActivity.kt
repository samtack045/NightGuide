package com.example.ng

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ng.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ContactItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val contactViewModel: ContactViewModel by viewModels {
        ContactItemModelFactory((application as ContactApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)


        binding.newContactButton.setOnClickListener{
            NewContactSheet(null).show(supportFragmentManager, "newContactTag")
        }

        binding.mapsButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }


        setRecyclerView()
    }

    private fun setRecyclerView()
    {
        val mainActivity = this
        contactViewModel.contactItems.observe(this){
            binding.contactListRecyclerView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = ContactItemAdapter(it as MutableList<ContactItem>, mainActivity)
            }
        }
    }

    override fun editContactItem(contactItem: ContactItem) {
        NewContactSheet(contactItem).show(supportFragmentManager, "newContactTag")
    }




}