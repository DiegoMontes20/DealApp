package mx.edu.utez.deal.chat2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.Model.LocationAddress
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityMapAppointmentBinding
import mx.edu.utez.deal.utils.LocationService
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit


class MapAppointmentActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapAppointmentBinding
    private var providerId: String? = null
    private var providerLocation: LocationAddress? = null
    private lateinit var myJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        providerId = intent.getStringExtra("providerId")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.regresarMapa.setOnClickListener {
            onBackPressed()
        }
    }

    fun init() {
        title = "Mapa"
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Toast.makeText(this, "Cargando ubicación del proveedor", Toast.LENGTH_SHORT).show()
        mMap.isMyLocationEnabled = true
        myJob = startRepeatingJob(8000)
    }

    private fun getProviderLocation() {
        val retrofit = Retrofit.Builder()
                .baseUrl(ConfIP.IP)
                .client(OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                    chain.proceed(request)
                }.build())
                .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {
            val response = service.getProviderLocation(providerId)
            runOnUiThread {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                    response.body()
                                            ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                    )
                    val jobject = JSONObject(prettyJson)
                    val locationJsonObject = jobject.getJSONObject("data")
                    providerLocation = gson.fromJson(locationJsonObject.toString(), LocationAddress::class.java)
                    val providerLatLng = LatLng(providerLocation!!.latitude, providerLocation!!.longitude)
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(providerLatLng).title(providerLocation!!.name))
                    val cameraPosition = CameraPosition.Builder()
                           .target(providerLatLng) // Sets the center of the map to Mountain View
                           .zoom(19f)            // Sets the zoom
                          .tilt(30f)            // Sets the tilt of the camera to 30 degrees
                         .build()              // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(this@MapAppointmentActivity, "Error al obtener la ubicación del proveedor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Main).launch(coroutineExceptionHandler.handler) {
            while (true) {
                getProviderLocation()
                delay(timeInterval)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        myJob .cancel()

    }
}