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
    var myLocation: Location? = null
    private val appointments: ArrayList<Appointment> = ArrayList()

    inner class LocationListener(provider: String?) : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            myLocation?.set(location)
        }

        init {
            myLocation = Location(provider)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldShareLocation(){
        val retrofit =getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.getAppointments()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    var jobject: JSONObject = JSONObject(prettyJson)
                    var Jarray: JSONArray = jobject.getJSONArray("data")
                    for(i in 0 until Jarray.length()){
                        var appointmentJson: JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val appointment: Appointment = gson.fromJson(appointmentJson.toString(), Appointment::class.java)
                        appointments.add(appointment)
                    }
                    //println(conversations)
                    var appointment = appointments.find { appointment ->  appointment.onWay}
                    if (appointment!= null){
                        var localDateTimeAppointment = LocalDateTime.parse(appointment.dateTime).plus(5, ChronoUnit.MINUTES)
                        var localDateTime = LocalDateTime.now()
                        while (localDateTime.isBefore(localDateTimeAppointment)){
                            val jsonObject = JSONObject()
                            jsonObject.put("name", "Proveedor")
                            jsonObject.put("latitude", myLocation!!.latitude)
                            jsonObject.put("longitude", myLocation!!.longitude)
                            val jsonObjectString = jsonObject.toString()
                            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
                            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
                            val service = retrofit.create(APIService::class.java)
                            CoroutineScope(Dispatchers.IO).launch{
                                val response = service.saveLocation(requestBody)
                                withContext(Dispatchers.Main){
                                    if(response.isSuccessful){
                                        Log.e("Location", "compartiendo ubicacion, lat: ${myLocation!!.latitude}, log: ${myLocation!!.longitude}")
                                    }else{
                                        Log.e("Error", response.code().toString())
                                    }
                                }
                            }
                            delay(5000)
                            localDateTime = localDateTime.plus(5, ChronoUnit.SECONDS)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = service.getAppointments()

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    val JSONObject = JSONObject()
                                    JSONObject.put("id", appointment.id)
                                    JSONObject.put("onWay", false)
                                    val jsonObjectString = JSONObject.toString()
                                    // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
                                    val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
                                    val service = retrofit.create(APIService::class.java)
                                    CoroutineScope(Dispatchers.IO).launch{
                                        val response = service.saveOnWay(requestBody)
                                        withContext(Dispatchers.Main){
                                            if(response.isSuccessful){
                                                Log.e("Appointment on way", "sett false exitoso")
                                            }else{
                                                Log.e("Error", response.code().toString())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    Toast.makeText(this@LocationService, "Error en la consulta :c", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
    }
}