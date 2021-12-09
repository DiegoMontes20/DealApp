package mx.edu.utez.deal.AppoinmentProcess

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityMapsBinding
import mx.edu.utez.deal.utils.LocationService
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object{
        var Maplat=""
        var Maplog=""
        var fecha=""
        var dialogAddress=""
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ubicacion.setOnClickListener {
            dialog()
            //startActivity(Intent(this,SummaryActivity::class.java))
        }

        binding.centrar.setOnClickListener {
            val punto = LatLng(LocationService.loc.latitude, LocationService.loc.longitude)
            println("punto ${punto}")
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(punto).title("Mi Ubicación"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(punto))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punto, 16.0f))

//            if(LocationService.loc.latitude==null || LocationService.loc.latitude==null){
//                Toast.makeText(this, "Por favor enciende la ubicación", Toast.LENGTH_SHORT).show()
//            }else{
//
//            }
            //Toast.makeText(this, "Centrar", Toast.LENGTH_LONG).show()
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(18.85, -99.20)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

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
        mMap.isMyLocationEnabled = false

        mMap.setOnCameraIdleListener {
            var lat = mMap.cameraPosition.target.latitude
            var long = mMap.cameraPosition.target.longitude
            Maplog=mMap.cameraPosition.target.longitude.toString()
            Maplat =mMap.cameraPosition.target.latitude.toString()

            encontrarDireccion(lat, long)
            //Toast.makeText(this, "Coordenadas -> lat: "+lat+", long: "+long,Toast.LENGTH_LONG).show()
        }
            buscarDireccion.setOnClickListener {
                if(direccion.text.isNotEmpty()){
                    encontrarPunto(direccion.text.toString())
                }
            }
    }
    fun dialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Esta seguro de agendar la cita?")
        builder.setMessage("¿Esta es la dirección correcta? \n ${dialogAddress}")
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            changeAct()
        })
        builder.setNegativeButton("Cancelar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    fun changeAct(){
        startActivity(Intent(this,SummaryActivity::class.java))
    }
    fun encontrarPunto(direccion:String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocationName(direccion, 5)


        if(address.isNotEmpty()) {
            var lat = address.get(0).latitude
            var long = address.get(0).longitude

            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, long), 16.0f
                )
            )
        }
    }

    fun encontrarDireccion(latitud:Double, longitude:Double){
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(latitud, longitude, 5)
        if(address.isNotEmpty()){
            var direccion= address[0].getAddressLine(0)
            println("Dirección -> ${direccion}")
            dialogAddress=direccion
            binding.textView2.setText("${direccion.substring(0,10)}...")

            //Toast.makeText(this,direccion, Toast.LENGTH_LONG ).show()
        }

    }
}