package com.example.ng

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ng.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ContactItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val contactViewModel: ContactViewModel by viewModels {
        ContactItemModelFactory((application as ContactApplication).repository)
    }



    @RequiresApi(Build.VERSION_CODES.O)
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

        binding.callFriendButton.setOnClickListener {
            callAFriend()
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

    override fun call(contactItem: ContactItem) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:" + Uri.encode(contactItem.num))
        startActivity(intent)
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun callAFriend(){
        val friends = contactViewModel.contactItems.value
        if (friends != null) {for (i in friends.indices) {
            // Make the call
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode(friends[i].num))
            startActivity(intent)

            Thread.sleep(15000)


        }
        }
    }





}