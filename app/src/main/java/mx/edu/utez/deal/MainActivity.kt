package mx.edu.utez.deal

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityMainBinding
import mx.edu.utez.deal.util.LocationService
import mx.edu.utez.deal.util.PermissionChecker
import mx.edu.utez.deal.util.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val appointments: ArrayList<Appointment> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val permissionChecker = PermissionChecker(this)
        if (permissionChecker.arePermissionsGranted()) {
            ///showToast("permisos ya concedidos uwu")
        } else {
            permissionChecker.requestPermissions()
            //showToast("solicitando permisos :c", 1)
        }

        startService(Intent(this, LocationService::class.java))

        shouldShareLocation()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldShareLocation(){
        val retrofit =getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
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
                    var appointment = appointments.find { appointment ->  appointment.onWay && appointment.approved && appointment.enabled}
                    if (appointment!= null){
                        var localDateTimeAppointment = LocalDateTime.parse(appointment.dateTime).plus(5, ChronoUnit.MINUTES)
                        var localDateTime = LocalDateTime.now()
                        while (localDateTime.isBefore(localDateTimeAppointment)){
                            val jsonObject = JSONObject()
                            jsonObject.put("name", "Proveedor")
                            jsonObject.put("latitude", LocationService.latLng.latitude)
                            jsonObject.put("longitude", LocationService.latLng.longitude)
                            val jsonObjectString = jsonObject.toString()
                            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
                            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
                            val service = retrofit.create(APIService::class.java)
                            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
                                val response = service.saveLocation(requestBody)
                                withContext(Dispatchers.Main){
                                    if(response.isSuccessful){
                                        Log.e("Location", "compartiendo ubicacion, lat: ${LocationService.latLng.latitude}, log: ${LocationService.latLng.longitude}")
                                    }else{
                                        Log.e("Error", response.code().toString())
                                    }
                                }
                            }
                            delay(5000)
                            localDateTime = localDateTime.plus(5, ChronoUnit.SECONDS)
                        }
                        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
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
                                    CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
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
                    Toast.makeText(this@MainActivity, "Error en la consulta :c", Toast.LENGTH_SHORT).show()
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