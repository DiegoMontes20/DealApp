package mx.edu.utez.deal.ui.dashboard

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
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapter.AppointmetAdapter
import mx.edu.utez.deal.adapter.ProviderAdapter
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.FragmentDashboardBinding
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val lista: ArrayList<AppointmentModel> = ArrayList()
    private lateinit var adapter: AppointmetAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.rvServices.layoutManager = LinearLayoutManager(activity)
        getData()

        binding.terminada.setOnClickListener {
            changeCategory( "Cita realizada")
        }

        binding.proceso.setOnClickListener {
            changeCategory("Cita programada")
        }

        binding.pendiente.setOnClickListener {
            changeCategory( "Cita por aprobar")
        }

        binding.canceladas.setOnClickListener {
            changeCategory("Canceladas")
        }


        return root
    }

    fun changeCategory(category: String) {
        when (category) {
            "Cita realizada" -> {
                binding.txtTitulo.text = "Cita realizada"
                adapter = AppointmetAdapter(lista.filter { appointmentModel -> appointmentModel.approved && !appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            "Cita programada" -> {
                binding.txtTitulo.text = "Cita programada"
                adapter = AppointmetAdapter(lista.filter { appointmentModel -> appointmentModel.approved && appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            "Cita por aprobar" -> {
                binding.txtTitulo.text = "Cita por aprobar"
                adapter = AppointmetAdapter(lista.filter { appointmentModel -> !appointmentModel.approved && appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            else -> {
                binding.txtTitulo.text = "Cita Cancelada"
                adapter = AppointmetAdapter(lista.filter { appointmentModel -> !appointmentModel.approved && !appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
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
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {

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
                    //Log.w("body", prettyJson)
                    val jobject:JSONObject = JSONObject(prettyJson)
                    val Jarray:JSONArray = jobject.getJSONArray("data")
                    for(i in 0 until Jarray.length()){
                        val provedor:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val providerList:AppointmentModel = gson.fromJson(provedor.toString(), AppointmentModel::class.java)
                        lista.add(providerList)
                    }
                    adapter = AppointmetAdapter(emptyList())
                    binding.rvServices.adapter = adapter
                    changeCategory("Cita programada")
                    if (lista.isEmpty()) {
                        binding.btnTexto.visibility = View.VISIBLE
                        Toast.makeText(activity, "No hay citas disponibles", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        binding.btnTexto.visibility = View.GONE
                    }
                } else {
                    val code = response.code().toString()
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