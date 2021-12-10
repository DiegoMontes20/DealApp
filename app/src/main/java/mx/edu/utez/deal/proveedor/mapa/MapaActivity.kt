package mx.edu.utez.deal.proveedor.mapa

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityMapa2Binding

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapa2Binding

    private var locationName: String? = null
    private var locationLatitude: Double = 0.0
    private var locationLongitude: Double = 0.0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapa2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        locationName = intent.getStringExtra("locationName")
        locationLatitude = intent.getDoubleExtra("locationLatitude", 0.0)
        locationLongitude = intent.getDoubleExtra("locationLongitude", 0.0)

        println("location: $locationName")
        println("locationlat: $locationLatitude")
        println("locationlong: $locationLongitude")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        print("mapa listoooooo")
        mMap = googleMap

        // Add a marker in client location and move the camera
        val clientLocation = LatLng(locationLatitude, locationLongitude)
        mMap.addMarker(MarkerOptions().position(clientLocation).title(locationName))
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPosition = CameraPosition.Builder()
            .target(clientLocation) // Sets the center of the map to Mountain View
            .zoom(18f)            // Sets the zoom
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.isMyLocationEnabled = true
    }
}

