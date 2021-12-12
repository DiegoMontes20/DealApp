package mx.edu.utez.deal.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapter.AppointmentAdapter
import mx.edu.utez.deal.databinding.FragmentHomeBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    companion object{
        var nombreEmpresa=""
    }

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    val lista:ArrayList<Appointment> = ArrayList()
    val listaRealizadas:ArrayList<Appointment> = ArrayList()
    val listaPendiente:ArrayList<Appointment> = ArrayList()
    val listaCanceladas:ArrayList<Appointment> = ArrayList()
    private lateinit var adapter: AppointmentAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var idProveedor=""
    val jsonObject = JSONObject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.btnCitas.visibility = View.GONE

        getData()
        getProfile()

        _binding!!.btnCitasPendientes.setOnClickListener {

            if (listaPendiente.isNotEmpty()) {
                adapter = AppointmentAdapter(listaPendiente)
                _binding!!.rvCitas.layoutManager =
                    LinearLayoutManager(activity)
                _binding!!.rvCitas.adapter=adapter
                adapter.notifyDataSetChanged()
                _binding!!.title.text = getString(R.string.agenda_pendiente)
                _binding!!.btnCitasPendientes.visibility = View.GONE
                _binding!!.btnCitasRealizadas.visibility = View.VISIBLE
                _binding!!.btnCitas.visibility = View.VISIBLE
                _binding!!.btnCitasCanceladas.visibility = View.VISIBLE
            }else
                Toast.makeText(activity,"No hay registro de citas pendientes",Toast.LENGTH_SHORT).show()
        }

        _binding!!.btnCitas.setOnClickListener {
            if (lista.isNotEmpty()) {
                adapter = AppointmentAdapter(lista)
                _binding!!.rvCitas.layoutManager =
                    LinearLayoutManager(activity)
                _binding!!.rvCitas.adapter=adapter
                adapter.notifyDataSetChanged()
                _binding!!.title.text = getString(R.string.agenda)
                _binding!!.btnCitasCanceladas.visibility = View.VISIBLE
                _binding!!.btnCitasPendientes.visibility = View.VISIBLE
                _binding!!.btnCitasRealizadas.visibility = View.VISIBLE
                _binding!!.btnCitas.visibility = View.GONE
            }else
                Toast.makeText(activity,"No hay registro de citas en agenda",Toast.LENGTH_SHORT).show()
        }

        _binding!!.btnCitasRealizadas.setOnClickListener {
            if (listaRealizadas.isNotEmpty()) {
                adapter = AppointmentAdapter(listaRealizadas)
                _binding!!.rvCitas.layoutManager =
                    LinearLayoutManager(activity)
                _binding!!.rvCitas.adapter=adapter
                adapter.notifyDataSetChanged()
                _binding!!.title.text = getString(R.string.agenda_realizada)
                _binding!!.btnCitasCanceladas.visibility = View.VISIBLE
                _binding!!.btnCitasPendientes.visibility = View.VISIBLE
                _binding!!.btnCitas.visibility = View.VISIBLE
                _binding!!.btnCitasRealizadas.visibility = View.GONE
            }else
                Toast.makeText(activity,"No hay registro de citas realizadas",Toast.LENGTH_SHORT).show()
        }

        _binding!!.btnCitasCanceladas.setOnClickListener {
            if (listaCanceladas.isNotEmpty()){
                adapter = AppointmentAdapter(listaCanceladas)
                _binding!!.rvCitas.layoutManager =
                    LinearLayoutManager(activity)
                _binding!!.rvCitas.adapter=adapter
                adapter.notifyDataSetChanged()
                _binding!!.title.text = getString(R.string.agenda_cancelada)
                _binding!!.btnCitasCanceladas.visibility = View.GONE
                _binding!!.btnCitasPendientes.visibility = View.VISIBLE
                _binding!!.btnCitas.visibility = View.VISIBLE
                _binding!!.btnCitasRealizadas.visibility = View.VISIBLE
            }else
                Toast.makeText(activity,"No hay registro de citas canceladas",Toast.LENGTH_SHORT).show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getProfile(){
        val retrofit = getRetrofit()

        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {

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

                    val jsonPro:JSONObject = JSONObject(jobject.get("data").toString())


                    idProveedor = jsonPro.get("id").toString()
                    nombreEmpresa = jsonPro.get("name").toString()
                    var token: String? = null
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("MyFirebaseMsgService->", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        token = task.result
                        jsonObject.put("id", idProveedor)
                        jsonObject.put("notificationToken", token)
                        //println(jsonObject)
                        update()

                    })


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    private fun update() {
        val retrofit = getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            val response = service.updateProfile(requestBody)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    Toast.makeText(activity, "Token actualizado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity, "Error al actualizar token", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(){
        val retrofit = getRetrofit()

        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            val response = service.getAppointments()

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
                    //Log.w("response", prettyJson)
                    var jobject: JSONObject = JSONObject(prettyJson)
                    var Jarray: JSONArray = jobject.getJSONArray("data")
                    var i=0
                    while (i < Jarray.length() ){
                        var provedor: JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val providerList:Appointment = gson.fromJson(provedor.toString(), Appointment::class.java)

                        if (providerList.approved.not() && providerList.enabled.not())
                            listaCanceladas.add(providerList)
                        else if (providerList.approved && LocalDateTime.parse(providerList.dateTime).isBefore(LocalDateTime.now()))
                            listaRealizadas.add(providerList)
                        else if (providerList.approved) lista.add(providerList)
                        else listaPendiente.add(providerList)
                        i++
                    }
                    if(lista.isEmpty()){
                        Toast.makeText(activity, "No hay registro de citas", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.rvCitas.layoutManager = LinearLayoutManager(activity)
                        adapter = AppointmentAdapter(lista)
                        _binding!!.rvCitas.layoutManager =
                            LinearLayoutManager(activity)
                        _binding!!.rvCitas.adapter=adapter
                        adapter!!.notifyDataSetChanged()
                    }


                } else {
                    var code = response.code().toString()
                    if(code == "401"){
                        Toast.makeText(activity, "La sesión ha expirado", Toast.LENGTH_SHORT).show()
                        PrefsApplication.prefs.deleteAll()
                        val intent = Intent(activity, LoginScreen::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }


    }



    fun getRetrofit():Retrofit{
        return  Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
    }

}