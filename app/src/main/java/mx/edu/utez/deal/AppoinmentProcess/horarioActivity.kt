package mx.edu.utez.deal.AppoinmentProcess

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_horario.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityHorarioBinding
import mx.edu.utez.deal.ui.dialog.DatePickerFragment
import mx.edu.utez.deal.utils.LocationService
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class horarioActivity : AppCompatActivity() {

    companion object{
        var fecha=""
    }



    private lateinit var binding:ActivityHorarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHorarioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.horaselect.setOnClickListener {
            if(binding.etBirthDate.text.isEmpty()){
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_LONG).show()
            }else{
                startService(Intent(this, LocationService::class.java))
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero

            val selectedDate = year.toString() + "-" +   (month + 1) + "-" + day.toString()

            fecha=selectedDate
            println("FECHA DEL CALENDAR -> ${fecha}")
            etBirthDate.setText(selectedDate)
            getData()
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun getData(){
        println("id -> ${DetailProvider.id}")
        println("fecha -> ${fecha}")
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.getAppointmentsHoras(fecha, DetailProvider.id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )
                    Toast.makeText(applicationContext, response.body().toString(), Toast.LENGTH_SHORT).show()
                    Log.d("Pretty Printed JSON :", prettyJson)

                } else {
                    Toast.makeText(applicationContext, "No hay un horario disponible en esta fecha, intenta otra fecha", Toast.LENGTH_LONG).show()
                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
}