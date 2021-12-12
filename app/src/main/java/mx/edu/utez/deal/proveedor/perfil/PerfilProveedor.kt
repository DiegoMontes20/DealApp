package mx.edu.utez.deal.proveedor.perfil

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityPerfilProveedorBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Base64


class PerfilProveedor : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilProveedorBinding

    val jsonObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getProfile()

    }

    fun getProfile() {
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {

            val jsonObjectString = jsonObject.toString()

            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
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
                    var jobject = JSONObject(prettyJson)

                    val jsonProveedor = JSONObject(jobject.get("data").toString())

                    val proveedor = gson.fromJson(jsonProveedor.toString(), Provider::class.java)

                    binding.inputNombreEmpresa.setText(proveedor.name)
                    binding.inputDescripcion.setText(proveedor.description)
                    binding.inputTelefono.setText(proveedor.phone)
                    binding.inputGiro.setText(proveedor.area)
                    binding.inputUbicacion.setText(if (proveedor.location == null) "Sin ubicación" else proveedor.location!!.name)
                    binding.inputDesde.setText(proveedor.startTime)
                    binding.inputHasta.setText(proveedor.finalTime)
                    val decodedString: ByteArray = Base64.decode(proveedor.image, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imgPerfilProvider.setImageBitmap(decodedByte)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error al obetener la información, intente más tarde",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
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