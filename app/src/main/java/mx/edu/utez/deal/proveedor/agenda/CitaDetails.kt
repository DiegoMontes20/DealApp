package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityCitaDetailsBinding
import mx.edu.utez.deal.proveedor.mapa.MapaActivity
import mx.edu.utez.deal.util.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class CitaDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCitaDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreCliente = intent.getStringExtra("nombreCliente")
        val idCita = intent.getStringExtra("idCita")
        val numeroCliente = intent.getStringExtra("telefonoCliente")
        val fechaHora = intent.getStringExtra("dateTime")
        val locationName = intent.getStringExtra("locationName")
        val onWay = intent.getBooleanExtra("onWay", false)
        val enabled = intent.getBooleanExtra("estado", false)
        val locationLatitude = intent.getDoubleExtra("locationLatitude", 0.0);
        val locationLongitude = intent.getDoubleExtra("locationLongitude", 0.0);

        binding.txtFechaHora.text = fechaHora
        binding.txtCliente.text = nombreCliente
        binding.txtPhone.text = numeroCliente

        if(enabled && !onWay)
            binding.btnEnCamino.visibility = View.VISIBLE
        else
            binding.btnEnCamino.visibility = View.GONE

        if (locationName.isNullOrEmpty())
            binding.btnMostrarUbicacion.visibility = View.GONE

        binding.btnMostrarUbicacion.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("locationName", locationName)
            intent.putExtra("locationLatitude", locationLatitude)
            intent.putExtra("locationLongitude", locationLongitude)
            startActivity(intent)
        }

        binding.btnRegresar.setOnClickListener {
            onBackPressed()
        }

        binding.btnEnCamino.setOnClickListener {
            val retrofit = getRetrofit()

            val service = retrofit.create(APIService::class.java)
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {

                val response = service.getAppointments()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val JSONObject = JSONObject()
                        JSONObject.put("id", idCita)
                        JSONObject.put("onWay", true)
                        val jsonObjectString = JSONObject.toString()
                        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
                        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
                        val service = retrofit.create(APIService::class.java)
                        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
                            val response = service.saveOnWay(requestBody)
                            withContext(Dispatchers.Main){
                                if(response.isSuccessful){
                                    Toast.makeText(this@CitaDetails, "Has notificado que vas en camino", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                }else{
                                    Log.e("Error", response.code().toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getRetrofit(): Retrofit {
        return  Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
    }
}