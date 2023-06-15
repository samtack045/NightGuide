package com.example.ng

import com.google.maps.android.PolyUtil
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.SpannableStringBuilder
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback, LocationListener, FaveLocationClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentPolyline : Polyline
    private val startLocation = LatLng(51.500801, -0.180550)
    private val destLocation = LatLng(51.49151686664713, -0.1939163228939211)
    private val start : MarkerOptions = MarkerOptions().position(startLocation).title("Huxley")
    private val destination : MarkerOptions = MarkerOptions().position(destLocation).title("Earls Court")
    private val markerPoints: ArrayList<LatLng> = ArrayList()
    private var mapJustLoaded = true

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)




        binding.contactsPageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buSOS.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode("123"))
            startActivity(intent)
        }

        val d1 = ContactItemDatabase.getDatabase(applicationContext)
        val dao = d1.faveLocationDao()
        val mapsActivity = this
        binding.buEditFave.setOnClickListener {

            fun editFave() = runBlocking {

                val len = dao.allFaveLocationItems().count()

                if (len == 0) {
                    NewFaveLocationSheet(null, mapsActivity).show(supportFragmentManager, "newHomeTag")
                } else {
                    dao.allFaveLocationItems().collect { list ->
                        NewFaveLocationSheet(list.first(), mapsActivity).show(
                            supportFragmentManager,
                            "editHomeTag"
                        )
                    }
                }
            }
            editFave()
        }

        binding.buHome.setOnClickListener {
            fun home() = runBlocking {
                dao.allFaveLocationItems().collect { list ->
                    val home = list.first()
                    val addresses: List<Address>?
                    val geocoder = Geocoder(mapsActivity, Locale.getDefault())
                    addresses = geocoder.getFromLocation(
                        home.latitude,
                        home.longitude,
                        1
                    )
                    val address = addresses!![0].getAddressLine(0)
                    binding.searchBox.text = SpannableStringBuilder.valueOf(address)
                }
            }
            home()
        }

        mapFragment.getMapAsync(this)
    }

//    override fun onResume() {
//        super.onResume()
//        val fave_locations = resources.getStringArray(R.array.fave_locations)
//        val arrayAdapter = ArrayAdapter(this, R.layout.fave_location_dropdown, fave_locations)
//        binding.autoCompleteTextView.setAdapter(arrayAdapter)
//    }

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
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 101)
        }


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)
        } catch (e: Exception) {
            //Log.d("mylog", "EXCEPTION")
        }

//        binding.searchBox.setOnEditorActionListener {
//
//        }

        binding.buRoute.setOnClickListener {
            setARoute()
            setMapLocation()
        }

        binding.buCentre.setOnClickListener {
            setMapLocation()
        }


        // Add markers for the predefined start and destination locations
        mMap.addMarker(start)
        mMap.addMarker(destination)

        // Enable the user's current location on the map
        mMap.isMyLocationEnabled = true
    }

    private fun setMapLocation() {
        mapFragment.getMapAsync { googleMap ->
            // Retrieve the current location
            val currentLocation = googleMap.myLocation
            val lat = currentLocation.latitude
            val long = currentLocation.longitude
            // Use the latitude and longitude as needed
            val current = LatLng(lat, long)
            Log.d("mylog", current.toString())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15f))
        }
    }

    private fun setARoute(){
        if (markerPoints.size > 0) {
            markerPoints.clear()
            mMap.clear()
        }

        val addr = binding.searchBox.text.toString()
        val geocode = Geocoder(this, Locale.getDefault())
        val addList = geocode.getFromLocationName(addr, 1)
        val latLng = LatLng(addList?.get(0)?.latitude!!, addList[0]?.longitude!!)

        // Adding new item to the ArrayList
        markerPoints.add(latLng)

        // Creating MarkerOptions
        val options = MarkerOptions()

        // Setting the position of the marker
        options.position(latLng)

        if (markerPoints.size == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        }

        // Add new marker to the map
        mMap.addMarker(options)
        var lat = 0.0
        var long: Double = 0.0
        var origin: LatLng
        // Checks whether start and end locations are captured
        if (markerPoints.size >= 1) {
            mapFragment.getMapAsync { googleMap ->
                // Retrieve the current location
                val currentLocation = googleMap.myLocation
                lat = currentLocation.latitude
                long = currentLocation.longitude
                // Use the latitude and longitude as needed
               // Log.d("mylog", "Current Latitude: $lat, Longitude: $long")
                origin = LatLng(lat, long)
                val dest = markerPoints[0]

                // Getting URL to the Google Directions API
                val sentPI: PendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent("SMS_SENT"),
                    PendingIntent.FLAG_IMMUTABLE
                )
                val url = getUrl(origin, dest, listOf(LatLng(51.498898, -0.199795)))
                val fetchUrl = FetchURL(this)

                fetchUrl.execute(url, "walking")
            }


        }}

    private fun getUrl(startLocation: LatLng, destLocation: LatLng, avoidLocations: List<LatLng>): String {
        val sentPI: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("SMS_SENT"),
            PendingIntent.FLAG_IMMUTABLE
        )

        val key = "AIzaSyAPoL0l_feaQrdNAFClruqteDXqSVpCMig"
        val str_start : String = "origin=" + startLocation.latitude + "," + startLocation.longitude
        val str_dest : String = "destination=" + destLocation.latitude + "," + destLocation.longitude
        val mode : String = "mode=walking"
        val avoid = "avoid=${avoidLocations.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }}"
        val parameters : String = "$str_start&$str_dest&$mode&$avoid"

        val output : String = "json"


        val xyz = "$output?$parameters&key=$key"
        val url = "https://maps.googleapis.com/maps/api/directions/" + xyz
        Log.d("mylog", url)

        return url
    }
    override fun onTaskDone(vararg values: Any?) {
        //if (currentPolyline != null) currentPolyline.remove()
        currentPolyline = mMap.addPolyline((values[0] as PolylineOptions?)!!)

    }

    override fun onLocationChanged(location: Location) {
        if (mapJustLoaded) {
            setMapLocation()
            mapJustLoaded = false
        }
        Log.d("mylog", "LOCATION CHANGED")
        try {
            val userLocation = LatLng(location.latitude, location.longitude)
            if (!PolyUtil.isLocationOnPath(userLocation, currentPolyline.points, true, 30.0)){
                // Notify the user or perform any desired actions
//            Log.d("mylog", repository.allContactItems.toString())
                val sentPI: PendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent("SMS_SENT"),
                    PendingIntent.FLAG_IMMUTABLE
                )
//            SmsManager.getDefault().sendTextMessage(contactItem?.num, null, "Leaving now!",
//                sentPI, null)
                SmsManager.getDefault().sendTextMessage("07894098083", null, "I have strayed from my route",
                    sentPI, null)
                Toast.makeText(this, "You have strayed from the route!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle the exception (e.g., log, display an error message, etc.)
            Log.d("mylog", "EXCEPTION1")
        }
    }


//         //Iterate over the points of the route polyline
//        if (currentPolyline.points.size > 0) {
//            for (point in currentPolyline.points) {
//                Log.d("mylog", "In loop")
//                val routePoint = LatLng(point.latitude, point.longitude)
//
//                // Calculate the distance between the user's location and the route point
//                val distance = calculateDistance(userLocation, routePoint)
//                Log.d("mylog", "NEW DISTANCE(S)")
//                Log.d("mylog", distance.toString())
//
//                // Define a threshold distance to consider as "straying"
//                val threshold = 100 // Adjust this value as needed
//
//                // If the distance exceeds the threshold, set the flag to indicate straying
//                if (distance < threshold) {
//                    hasNotStrayed = true
//                    break
//                }
//            }
//        }else{
//            Log.d("mylog", "No points, I'll stop")
//        }


        //  Handle the case when the user has strayed from the route




    private fun calculateDistance(location1: LatLng, location2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            results
        )
        return results[0]
    }

    override fun editFaveLocation(faveLocationItem: FaveLocationItem) {
        NewFaveLocationSheet(faveLocationItem, this).show(supportFragmentManager, "newHomeTag")
    }

}