package mx.edu.utez.deal.InfoPersonal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Model.ClientProfile
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityEditarInformacioBinding
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class EditarInformacioActivity : AppCompatActivity() {
    private lateinit var binding :ActivityEditarInformacioBinding
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarInformacioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getProfile()
        // Create JSON using JSONObject

        binding.guardar.setOnClickListener {
            update()
        }

    }

    fun update(){
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()

        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {
            val jsonObject = JSONObject()
            jsonObject.put("id", id)
            jsonObject.put("name", binding.reNombre.text.toString())
            jsonObject.put("lastname", binding.reApellidos.text.toString())
            jsonObject.put("phone", binding.reTelefono.text.toString())
            println(jsonObject)
            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            val response = service.updateProfile(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    //Toast.makeText(activity, "Consulta con éxito", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(applicationContext, "Perfil actualizado", Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }else{
                        Toast.makeText(applicationContext, "Error al actualizar", Toast.LENGTH_LONG).show()
                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


    fun getProfile(){
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()

        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {

            val response = service.getProfile()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    //Toast.makeText(activity, "Consulta con éxito", Toast.LENGTH_LONG).show()
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    var jobject:JSONObject = JSONObject(prettyJson)
                    //jobject.get("data")
                    //println(jobject.get("data"))
                    val gson2 = Gson()
                    val clientProfile:ClientProfile = gson2.fromJson(jobject.get("data").toString(), ClientProfile::class.java)
                    binding.reApellidos.setText(clientProfile.lastname)
                    binding.reTelefono.setText(clientProfile.phone)
                    binding.reNombre.setText(clientProfile.name)
                    id=clientProfile.id

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
}