package com.example.ng

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.getDirection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ng.databinding.ActivityMapsBinding
import com.example.ng.directionhelpers.FetchURL
import com.example.ng.directionhelpers.TaskLoadedCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentPolyline : Polyline
    private val startLocation = LatLng(51.500801, -0.180550)
    private val destLocation = LatLng(51.49151686664713, -0.1939163228939211)
    private val start : MarkerOptions = MarkerOptions().position(startLocation).title("Huxley")
    private val destination : MarkerOptions = MarkerOptions().position(destLocation).title("Earls Court")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buRoute.setOnClickListener {
            println(getUrl( startLocation, destLocation))
            Toast.makeText(this@MapsActivity, getUrl( startLocation, destLocation), Toast.LENGTH_LONG).show()
//            FetchURL(this@MapsActivity).execute(
//                getUrl(
//                    startLocation,
//                    destLocation,
//                ), "walking"
//            )
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.contactsPageButton?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buSOS.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode("123"))
            startActivity(intent)
        }

        val url = getUrl(startLocation, destLocation)
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.addMarker(start)
        mMap.addMarker(destination)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 15f))
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true
    }

    private fun getUrl(startLocation: LatLng, destLocation: LatLng): String {
        val key = "AIzaSyAPoL0l_feaQrdNAFClruqteDXqSVpCMig"
        val str_start : String = "origin=" + startLocation.latitude + "," + startLocation.longitude
        val str_dest : String = "destination=" + destLocation.latitude + "," + destLocation.longitude
        val mode : String = "mode=walking"

        val parameters : String = "$str_start&$str_dest&$mode"

        val output : String = "json"

        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$key"
        return url
    }
    override fun onTaskDone(vararg values: Any?) {
        if (currentPolyline != null) currentPolyline.remove()
        currentPolyline = mMap.addPolyline((values[0] as PolylineOptions?)!!)
    }
}