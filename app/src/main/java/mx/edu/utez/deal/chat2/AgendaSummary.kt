package mx.edu.utez.deal.chat2

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.AppoinmentProcess.MapsActivity
import mx.edu.utez.deal.MainActivity
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityAgendaSummaryBinding
import mx.edu.utez.deal.utils.LocationService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit


class AgendaSummary : AppCompatActivity() {

    companion object {
        var idProveedor = ""
        var latIntent = 0.0
        var longIntent = 0.0
        var idCita =""
    }

    private lateinit var binding: ActivityAgendaSummaryBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAgendaSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciar()

        val parametros = this.intent.extras

        idProveedor = parametros!!.getString("idProveedor", null)
        latIntent = parametros.getDouble("latitude", 0.0)
        longIntent = parametros.getDouble("longitude", 0.0)

        binding.fechaHora.setText(parametros.getString("fechaHora"))
        binding.ubicacion.setText(parametros.getString("ubicacion"))
        binding.servicio.setText(parametros.getString("descripcion"))
        binding.nombreProveedor.setText(parametros.getString("nombreProveedor"))
        binding.tipoempresa.setText(parametros.getString("tipoServicio"))

        idCita = parametros.getString("idCita").toString()

        val approved = parametros.getBoolean("approved")
        val enabled = parametros.getBoolean("enabled")
        val onWay = parametros.getBoolean("onWay")

        binding.abrirMapa.visibility = View.GONE
        binding.calificar.visibility = View.GONE
        binding.btnCancelar.visibility = View.GONE
        binding.btnChat.visibility = View.GONE

        if(approved && !enabled){
            binding.txtTitle.text = "Cita realizada"
            binding.txtTitle.setTextColor(getColor(R.color.primary))

        }else if(approved && enabled){
            binding.txtTitle.text = "Cita programada"
            binding.txtTitle.setTextColor(getColor(R.color.primary))
            binding.calificar.visibility = View.VISIBLE
            binding.btnCancelar.visibility = View.VISIBLE
            binding.btnChat.visibility = View.VISIBLE
            if (onWay)
                binding.abrirMapa.visibility = View.VISIBLE
        } else if(!approved && enabled){
            binding.txtTitle.text = "Cita por aprobar"
            binding.txtTitle.setTextColor(getColor(R.color.primary))
            binding.btnCancelar.visibility = View.VISIBLE
            binding.btnChat.visibility = View.VISIBLE
        }else{
            binding.txtTitle.text = "Cita cancelada"
            binding.txtTitle.setTextColor(getColor(R.color.red))
        }


        binding.calificar.setOnClickListener {
            showDialog()
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("idProvider", idProveedor);
            intent.putExtra("nombre", parametros.getString("nombreProveedor"));
            startActivity(intent)
        }

        binding.abrirMapa.setOnClickListener {
            startService(Intent(this, LocationService::class.java))
            val intent = Intent(this, MapAppointmentActivity::class.java)
            intent.putExtra("providerId", idProveedor)
            startActivity(intent)
        }

        binding.btnCancelar.setOnClickListener {
            dialog()
        }
    }

    private fun iniciar() {
        title = "Resumen Agenda"
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.itemevaluation)
        val body = dialog.findViewById(R.id.numero) as EditText

        val yesBtn = dialog.findViewById(R.id.guardar) as Button
        val noBtn = dialog.findViewById(R.id.cerrar) as Button
        yesBtn.setOnClickListener {
            if(body.text.isEmpty()){
                showToast("Ingresa una calificación")
            }else{
                calificar(body.text.toString().toInt())
                dialog.dismiss()
            }
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun calificar(calificacion:Int){
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)
        val objEnviar = JSONObject()
        objEnviar.put("provider", idProveedor)
        objEnviar.put("appointment", idCita)
        objEnviar.put("evaluation", calificacion)
        val jsonObjectString = objEnviar.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.updateAppoinment(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                    response.body()
                                            ?.string()
                            )
                    )
                    Log.w("body", prettyJson)
                    showToast("Servicio calificado")
                    changeActity()
                }else{
                    showToast("Error al calificar")
                }
            }
        }
    }

    fun cancel(){
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val objEnviar = JSONObject()
            objEnviar.put("id", idCita)
            val jsonObjectString = objEnviar.toString()
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            withContext(Dispatchers.Main){
                val response = service.deleteAppoinment(requestBody)
                if(response.isSuccessful){
                    showToast("Se ha cancelado la cita")
                    changeActity()
                }else{
                    showToast("Ocurrió un error al cancelar la cita")
                }
            }
        }
    }

    fun dialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cancelar cita")
        builder.setMessage("¿Está seguro de cancelar la cita?")
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            cancel()
        })
        builder.setNegativeButton("Cancelar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    fun changeActity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun showToast(mensaje:String){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
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