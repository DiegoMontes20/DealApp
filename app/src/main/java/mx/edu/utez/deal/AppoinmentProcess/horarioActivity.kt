package mx.edu.utez.deal.AppoinmentProcess

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_horario.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapter.ItemTimeAdapter
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityHorarioBinding
import mx.edu.utez.deal.ui.dialog.DatePickerFragment
import mx.edu.utez.deal.utils.LocationService
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class horarioActivity : AppCompatActivity() {

    companion object {
        var fecha = ""

    }

    private lateinit var adapter: ItemTimeAdapter

    private lateinit var binding: ActivityHorarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHorarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ItemTimeAdapter(emptyList())
        val gridLayoutManager = GridLayoutManager(this, 4)


        binding.horas.layoutManager = gridLayoutManager
        binding.horas.adapter = adapter



        binding.etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.horaselect.setOnClickListener {
            if (binding.etBirthDate.text.isEmpty() || DetailProvider.tiempo.equals("")) {
                Toast.makeText(this, "Selecciona una fecha y una hora", Toast.LENGTH_LONG).show()
            } else {
                dialog()
            }
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero

                val selectedDate = year.toString() + "-" + (month + 1) + "-" + day.toString()

                fecha = selectedDate
                println("FECHA DEL CALENDAR -> ${fecha}")
                etBirthDate.setText(selectedDate)
                getData()
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun dialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación de horario y fecha")
        builder.setMessage("¿Está seguro de agendar en la fecha ${fecha} y la hora ${DetailProvider.tiempo}?")
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
           changeAct()
        })
        builder.setNegativeButton("Cancelar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    fun changeAct(){
        startService(Intent(this, LocationService::class.java))
        startActivity(Intent(this, MapsActivity::class.java))
    }


    fun getData() {
        println("id -> ${DetailProvider.id}")
        println("fecha -> ${fecha}")
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
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

                   // Log.d("Pretty Printed JSON :", prettyJson)
                    val jobject: JSONObject = JSONObject(prettyJson)
                    val Jarray: JSONArray = jobject.getJSONArray("data")
                    val times: ArrayList<String> = ArrayList()
                    for (i: Int in 0 until Jarray.length()){
                        times.add(Jarray.getString(i))
                    }
                    adapter.times = times
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "No hay un horario disponible en esta fecha, intenta otra fecha",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
}