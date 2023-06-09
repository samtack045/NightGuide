package com.example.ng

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ng.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity(), ContactItemClickListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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

//        binding.mapsButton.setOnClickListener{
//            val intent = Intent(this, MapsActivity::class.java)
//            startActivity(intent)
//        }

        binding.callFriendButton.setOnClickListener {
            callAFriend()
        }

        binding.butSOS.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode("123"))
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

    override fun msg(contactItem: ContactItem) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val task = fusedLocationProviderClient.lastLocation
        var lat = 0.0
        var longitude = 0.0

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
        val sentPI: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("SMS_SENT"),
            PendingIntent.FLAG_IMMUTABLE
        )
        val addresses: List<Address>?
        task.addOnSuccessListener {
            if (it != null) {
                lat = it.latitude
                longitude = it.longitude
            }
            val addresses: List<Address>?
            val geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                lat,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            NewMessageSheet(contactItem, sentPI, addresses).show(supportFragmentManager, "newMessageTag")

        }
    }

    override fun call(contactItem: ContactItem) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:" + Uri.encode(contactItem.num))
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun callAFriend(){
        val scope = CoroutineScope(Dispatchers.Default)
        val friends = contactViewModel.contactItems.value
        scope.launch {
            if (friends != null) {for (i in friends.indices) {
                // Make the call
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + Uri.encode(friends[i].num))
                startActivity(intent)
                Thread.sleep(30000)
            }
        }
        }
    }





}