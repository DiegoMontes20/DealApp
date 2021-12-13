package mx.edu.utez.deal.AppoinmentProcess

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
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.MainActivity
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivitySummaryBinding
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regresar.setOnClickListener {
            onBackPressed()
        }
//        println(MapsActivity.Maplat)
//        println(MapsActivity.Maplog)
        binding.fechaHora.setText("${horarioActivity.fecha} ${DetailProvider.tiempo}")
        binding.ubicacion.setText(MapsActivity.dialogAddress)
        binding.nombreProveedor.setText(DetailProvider.nombrePro)
        binding.tipoempresa.setText(DetailProvider.tipo)
        binding.servicio.setText(DetailProvider.descripcion)

        binding.aceptar.setOnClickListener {
            registerAppointment()
        }

    }

    fun registerAppointment(){

        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
        val service = retrofit.create(APIService::class.java)



        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            val provider = JSONObject()
            provider.put("id", DetailProvider.id)

            val appoinment = JSONObject()
            appoinment.put("provider", provider)
            appoinment.put("dateTime",horarioActivity.fecha+"T"+DetailProvider.tiempo)
            appoinment.put("takeout",true)
            appoinment.put("onWay", false)
            val jsonDireccion = JSONObject()
            jsonDireccion.put("name", MapsActivity.dialogAddress)
            jsonDireccion.put("latitude", MapsActivity.Maplat)
            jsonDireccion.put("longitude", MapsActivity.Maplog)
            appoinment.put("location",jsonDireccion)
            println(appoinment)

            val jsonObjectString = appoinment.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            val response = service.saveAppointment(requestBody)

            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    //Log.w("Response", prettyJson)
                    var jobject:JSONObject = JSONObject(prettyJson)
                    var message = jobject.get("message")
                    if(message.equals("Operación exitosa")){
                        Toast.makeText(applicationContext, "Cita creada", Toast.LENGTH_LONG).show()
                        changeActivity()
                    }else{
                        Toast.makeText(applicationContext, "Error al registrar la cita", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }
    }

    fun changeActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}