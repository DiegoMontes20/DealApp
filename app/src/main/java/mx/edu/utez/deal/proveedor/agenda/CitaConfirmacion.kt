package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.MainActivity
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityCitaConfirmacionBinding
import mx.edu.utez.deal.util.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class CitaConfirmacion : AppCompatActivity() {
    var idCita=""
    private lateinit var binding: ActivityCitaConfirmacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaConfirmacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idCita = getIntent().getStringExtra("idCita").toString();
        val nombreCliente = getIntent().getStringExtra("nombreCliente");
        val numeroCliente = getIntent().getStringExtra("telefonoCliente");
        val fechaHora = getIntent().getStringExtra("dateTime");
        val ubicacion = getIntent().getStringExtra("locationName");
        val estado = getIntent().getBooleanExtra("estado",true)

        println("estado>>>$estado")
        //binding.btnConfirmarCita.isVisible=!estado
        //binding.btnCancelarCita.isVisible=estado


        binding.infoFechaHora.text = "${fechaHora?.substring(0,10)} Hora: ${fechaHora?.substring(11,16)}"
        binding.infoCliente.text = nombreCliente
        binding.infoPhone.text = numeroCliente
        binding.address.text=ubicacion

        binding.btnCancelarCita.setOnClickListener {
            //startActivity(Intent(this, PendienteList::class.java))
            cancelCita()
        }

        binding.btnConfirmarCita.setOnClickListener {
            Toast.makeText(this, "Confirmando cita: $idCita", Toast.LENGTH_LONG).show()
            acceptCita()
            //
        }

    }

    fun cancelCita(){
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            val objEnviar = JSONObject()
            objEnviar.put("id", idCita)
            objEnviar.put("approved", false)
            val jsonObjectString = objEnviar.toString()
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            val response = service.updateAppointment(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    showToast("Se ha cancelado la cita")
                    changeActity2()
                }else{
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }
    }
    fun showToast(mensaje:String){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    fun acceptCita(){
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            val objEnviar = JSONObject()
            objEnviar.put("id", idCita)
            objEnviar.put("approved", true)

            val jsonObjectString = objEnviar.toString()
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            val response = service.updateAppointment(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    showToast("Se ha aceptado la cita")
                    changeActity()
                }else{
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }
    }

    fun changeActity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

    }

    fun changeActity2(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
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