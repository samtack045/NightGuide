package com.example.ng

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
    private var destLat = 100.0
    private var destLong = 100.0
    private var addr = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myLog", "creating main...")
        destLat = intent.extras?.getDouble("Latitude")?: 100.0
        destLong = intent.extras?.getDouble("Longitude")?: 100.0
        addr = intent.extras?.getString("Address")?: ""
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.newContactButton.setOnClickListener{
            NewContactSheet(null).show(supportFragmentManager, "newContactTag")
        }

        binding.mapsButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            Log.d("myLo", "Main" + destLat.toString())
            Log.d("myLo", "Main" + destLong.toString())
            intent.putExtra("Latitude", destLat)
            intent.putExtra("Longitude", destLong)
            intent.putExtra("Address", addr)
            val nums: List<String> = contactViewModel.contactItems.value
                ?.filter {it.isEmergencyContact}
                ?.map {it.num}
                ?: emptyList()
            intent.putStringArrayListExtra("Emergency Contacts", ArrayList(nums))
            startActivity(intent)
        }

        binding.callFriendButton.setOnClickListener {
            callAFriend()
        }

        binding.butSOS.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode("123"))
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            val intent = Intent(this, MapsActivity::class.java)
            Log.d("myLo", "Main" + destLat.toString())
            Log.d("myLo", "Main" + destLong.toString())
            intent.putExtra("Latitude", destLat)
            intent.putExtra("Longitude", destLong)
            intent.putExtra("Address", addr)
            val nums: List<String> = contactViewModel.contactItems.value
                ?.filter {it.isEmergencyContact}
                ?.map {it.num}
                ?: emptyList()
            intent.putStringArrayListExtra("Emergency Contacts", ArrayList(nums))
            startActivity(intent)
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        }
        setRecyclerView()
        Log.d("myLog", "creating main finished...")
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