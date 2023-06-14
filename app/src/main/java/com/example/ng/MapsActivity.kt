package com.example.ng

import com.google.maps.android.PolyUtil
import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.ng.databinding.ActivityMapsBinding
import com.example.ng.directionhelpers.FetchURL
import com.example.ng.directionhelpers.TaskLoadedCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentPolyline : Polyline
    private val startLocation = LatLng(51.500801, -0.180550)
    private val destLocation = LatLng(51.49151686664713, -0.1939163228939211)
    private val markerPoints: ArrayList<LatLng> = ArrayList()
    private var mapJustLoaded = true
    private var currDest: LatLng? = null
    private var addr = ""
    var offRoute = false

    var currMark: Marker? = null

    private lateinit var mapFragment: SupportMapFragment

    private var lastContacted = HashMap<String, Long>()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var incidentPoints: MutableList<IncidentPoint>

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
            Log.d("myLo", "contact hit" + (currDest?.latitude?:100.0).toString())
            Log.d("myLo", "contact hit" + (currDest?.latitude?:100.0).toString())
            intent.putExtra("Latitude", currDest?.latitude?:100.0)
            intent.putExtra("Longitude", currDest?.longitude?:100.0)
            intent.putExtra("Address", addr)
            startActivity(intent)
        }

        binding.buSOS.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + Uri.encode("123"))
            startActivity(intent)
        }
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
    fun getMap(): GoogleMap {
        return mMap
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("myLog", "map ready")
        mMap = googleMap
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 101)
            return
        }


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)
        } catch (e: Exception) {
            //Log.d("mylog", "EXCEPTION")
        }

        binding.buRoute.setOnClickListener {
            addr = binding.searchBox.text.toString()
            if (addr != "") {
                val geocode = Geocoder(this, Locale.getDefault())
                val addList = geocode.getFromLocationName(addr, 1)
                val latLng = LatLng(addList?.get(0)?.latitude!!, addList[0]?.longitude!!)
                setARoute(latLng)
            }
            setMapLocation()
        }

        binding.buCentre.setOnClickListener {
            setMapLocation()
        }

        // Enable the user's current location on the map
        mMap.isMyLocationEnabled = true

        val lat = intent.extras?.getDouble("Latitude") ?: 100.0
        val long = intent.extras?.getDouble("Longitude") ?: 100.0
        Log.d("myLo", "Maps before dest$lat")
        Log.d("myLo", "Maps before dest$long")
        if (!(lat == 100.0 && long == 100.0)) {
            currDest = LatLng(lat, long)
            Log.d("myLog", "route set")
            setARoute(currDest!!)
        }
        setMapLocation()
        val destAddress = intent.extras?.getString("Address") ?: ""
        if (destAddress != "") {
            val editable = Editable.Factory.getInstance()
            binding.searchBox.text = editable.newEditable(destAddress)
        }


        mMap.setOnMapClickListener {
            val d1 = ContactItemDatabase.getDatabase(applicationContext)
            val ipdao = d1.IPDao()
            ReportIncidentSheet(it, this, ipdao).show(supportFragmentManager, "newReportIncidentTag")
        }

        //val db = Room.databaseBuilder(applicationContext, ContactItemDatabase::class.java, "contact_item_database")

        val d1 = ContactItemDatabase.getDatabase(applicationContext)
        val ipdao = d1.IPDao()
        val m = mutableListOf<IncidentPoint>()
        lifecycleScope.launch{
            ipdao.allPoints().collect{v ->
                Log.d("myLog", "this is v" + v.toString())
                    m.addAll(v)

                Log.d("myLog", "this is m" + m.toString())
                //  for (p in m){
                //Log.d("myLog", p.toString())
                for (p in m) {
                    mMap.addMarker(MarkerOptions().position(LatLng(p.lat, p.long)).title("Incident Reported"))
                  //  incidentPoints.add(p)
                }
            }
        }




    }

    @SuppressLint("MissingPermission")
    private fun setMapLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync { googleMap ->
            // Retrieve the current location
            val currentLocation = fusedLocationProviderClient.lastLocation
            currentLocation.addOnSuccessListener {
                if (it != null) {
                    val lat = it.latitude
                    val long = it.longitude
                    val current = LatLng(lat, long)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15f))
                }

            }
        }
    }




    @SuppressLint("MissingPermission")
    fun setARoute(latLng: LatLng){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (markerPoints.size > 0) {
            markerPoints.clear()
            currentPolyline.remove()
            currMark?.remove()
        }

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
        currMark = mMap.addMarker(options)
        var lat = 0.0
        var long: Double = 0.0
        var origin: LatLng
        // Checks whether start and end locations are captured
        if (markerPoints.size >= 1) {
            mapFragment.getMapAsync { googleMap ->
                // Retrieve the current location
                val currentLocation = fusedLocationProviderClient.lastLocation
                currentLocation.addOnSuccessListener {
                    if (it != null) {
                        lat = it.latitude
                        long = it.longitude
                    }
                    Log.d("mylog", "Current Latitude: $lat, Longitude: $long")
                    origin = LatLng(lat, long)
                    val dest = markerPoints[0]
                    currDest = dest

                    // Getting URL to the Google Directions API
                    val sentPI: PendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent("SMS_SENT"),
                        PendingIntent.FLAG_IMMUTABLE
                    )


                    val url = getUrl(origin, dest)
                    val t = this
                    lifecycleScope.launch{
                        val db = ContactItemDatabase.getDatabase(applicationContext)
                        val ipDao = db.IPDao()
                        val badPoints = mutableListOf<LatLng>()
                        ipDao.allPoints().collect{l ->
                            val l1 = l.map {
                                LatLng(it.lat, it.long)
                            }
                            badPoints.addAll(l1)
                            Log.d("myLog", "mybadpts" + badPoints.toString())
                            val fetchUrl = FetchURL(t, badPoints)
                            fetchUrl.execute(url, "walking")
                        }


                    }
                }
                // Use the latitude and longitude as needed
            }
        }
    }
    fun createCircularArea(center: LatLng, radius: Double, numberOfPoints: Int): List<LatLng> {
        val latLngs = mutableListOf<LatLng>()
        val centerLat = center.latitude
        val centerLng = center.longitude

        val distanceRadians = radius / 6371.0 // Earth's radius in kilometers
        val angleStep = 2 * PI / numberOfPoints

        for (i in 0 until numberOfPoints) {
            val angle = i * angleStep
            val lat = asin(sin(centerLat) * cos(distanceRadians) + cos(centerLat) * sin(distanceRadians) * cos(angle))
            val lng = centerLng + atan2(sin(angle) * sin(distanceRadians) * cos(centerLat),
                cos(distanceRadians) - sin(centerLat) * sin(lat))
            latLngs.add(LatLng(lat * 180 / PI, lng * 180 / PI))
        }
        return latLngs
    }

    private fun getUrl(startLocation: LatLng, destLocation: LatLng): String {
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
      //  val avoid = "avoid=${avoidLocations.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }}"
        val parameters : String = "$str_start&$str_dest&$mode&alternatives=true"

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
        val sentPI: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("SMS_SENT"),
            PendingIntent.FLAG_IMMUTABLE
        )
        try {
            val userLocation = LatLng(location.latitude, location.longitude)
            if (!PolyUtil.isLocationOnPath(userLocation, currentPolyline.points, true, 1.0)){
                if (!offRoute) {
                    NewDeviationSheet(sentPI, intent.extras?.getStringArrayList("Emergency Contacts"), destLocation, this).show(supportFragmentManager, "newDeviationTag")
                }
            } else {
                if (currDest != null) {
                    if (BigDecimal(location.latitude).setScale(3) == BigDecimal(
                            currDest!!.latitude).setScale(3) && BigDecimal(location.longitude).setScale(3) == BigDecimal(location.longitude).setScale(3)) {
                        RouteCompleteSheet(sentPI, intent.extras?.getStringArrayList("Emergency Contacts")).show(supportFragmentManager, "newDeviationTag")
                    }
                }
            }
        } catch (e: Exception) {
            // Handle the exception (e.g., log, display an error message, etc.)
            Log.d("mylog", "EXCEPTION1")
        }
    }

    private fun calculateDistance(location1: LatLng, location2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            results
        )
        return results[0]
    }

}