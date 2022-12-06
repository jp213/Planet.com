package com.example.planet

import android.Manifest
import android.app.SearchManager
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.planet.databinding.ActivityMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import java.io.IOException
import java.sql.Connection
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var imageViewSearch : ImageView
    private lateinit var inputLocation: EditText
    lateinit var addressList : List<Address>
    private lateinit var latLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)//(R.layout.activity_maps)

        imageViewSearch = findViewById(R.id.imageViewSearch)
        inputLocation = findViewById(R.id.inputLocation)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocationUser()

        binding.imageViewSearch.setOnClickListener() { onButtonClick() }
    }

    private fun onButtonClick(){
        val location = inputLocation.text.toString()
        if(location==null){
            Toast.makeText(this,"Type any location name",Toast.LENGTH_SHORT).show()
        }else{
            var geocoder = Geocoder(this, Locale.getDefault())

            try {
                addressList = geocoder.getFromLocationName(location,1)
                if (addressList.isNotEmpty()){
                    latLng = LatLng(addressList[0].latitude,addressList[0].longitude)

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.mapFragment) as SupportMapFragment
                    mapFragment.getMapAsync(this)


                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentLocationUser() {
        if(ActivityCompat.checkSelfPermission
                (this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),permissionCode)
            return
        }


        val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location ->

            if(location != null){
                currentLocation = location

                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.mapFragment) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            permissionCode -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocationUser()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.mMap = googleMap
        this.mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE


        if(latLng == null) {
            latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        }

        val markerOptions = MarkerOptions().position(latLng).title("Searched Location")
        this.mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        this.mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,7f))
        this.mMap?.addMarker(markerOptions)

    }
}