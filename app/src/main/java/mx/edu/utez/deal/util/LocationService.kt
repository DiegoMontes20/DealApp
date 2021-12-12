package mx.edu.utez.deal.util

import android.Manifest
import android.app.Service
import android.location.LocationManager
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.util.LocationService
import android.content.Intent
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapterChat.AdapterConversation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import java.util.ArrayList

class LocationService : Service() {
    private var locationManager: LocationManager? = null
    companion object {
        var latLng = LatLng(0.0,0.0)
    }
    private val appointments: ArrayList<Appointment> = ArrayList()

    inner class LocationListener(provider: String?) : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            latLng = LatLng(location.latitude,location.longitude)
        }

    }

    var mLocationListener = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        iniLocalLocation()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000, 0f,
            mLocationListener[1]
        )
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 1000, 0f,
            mLocationListener[0]
        )
    }

    private fun iniLocalLocation() {
        if (locationManager == null) {
            locationManager =
                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            for (i in mLocationListener.indices) {
                locationManager!!.removeUpdates(mLocationListener[i])
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}