package mx.edu.utez.deal.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapter.AppointmentAdapter
import mx.edu.utez.deal.databinding.FragmentHomeBinding
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getData()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getData(){
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()

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

                    var jobject: JSONObject = JSONObject(prettyJson)
                    var Jarray: JSONArray = jobject.getJSONArray("data")
                    var i=0
                    var lista:ArrayList<Appointment> = ArrayList<Appointment>()
                    while (i < Jarray.length() ){
                        var provedor: JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val providerList:Appointment = gson.fromJson(provedor.toString(), Appointment::class.java)
                        lista.add(providerList)
                        i++
                    }
                    if(lista.isEmpty()){
                        Toast.makeText(activity, "No hay proveedores disponibles", Toast.LENGTH_LONG).show()
                    }else{
                        binding.rvCitas.layoutManager = LinearLayoutManager(activity)
                        val adapter = AppointmentAdapter(lista)
                        binding.rvCitas.adapter=adapter
                        adapter!!.notifyDataSetChanged()
                    }

                } else {
                    var code = response.code().toString()
                    if(code == "401"){
                        Toast.makeText(activity, "La sesión ha expirado", Toast.LENGTH_LONG).show()
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

}