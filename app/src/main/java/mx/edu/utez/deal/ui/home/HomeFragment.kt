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
import mx.edu.utez.deal.util.coroutineExceptionHandler
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
    private val lista: ArrayList<Appointment> = ArrayList()
    private lateinit var adapter: AppointmentAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var idProveedor=""
    val jsonObject = JSONObject()

    var tokenService =""

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

        binding.rvServices.layoutManager = LinearLayoutManager(activity)
        getData()
        getProfile()

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
                adapter = AppointmentAdapter(lista.filter { appointmentModel -> appointmentModel.approved && !appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            "Cita programada" -> {
                binding.txtTitulo.text = "Cita programada"
                adapter = AppointmentAdapter(lista.filter { appointmentModel -> appointmentModel.approved && appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            "Cita por aprobar" -> {
                binding.txtTitulo.text = "Cita por aprobar"
                adapter = AppointmentAdapter(lista.filter { appointmentModel -> !appointmentModel.approved && appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            else -> {
                binding.txtTitulo.text = "Cita Cancelada"
                adapter = AppointmentAdapter(lista.filter { appointmentModel -> !appointmentModel.approved && !appointmentModel.enabled })
                binding.rvServices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun getProfile(){
        val retrofit = getRetrofit()

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

                    val jsonPro:JSONObject = JSONObject(jobject.get("data").toString())


                    idProveedor = jsonPro.get("id").toString()
                    nombreEmpresa = jsonPro.get("name").toString()
                    tokenService = jsonPro.getString("notificationToken")
                    var token: String?
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("MyFirebaseMsgService->", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        token = task.result
                        if(!token.equals(tokenService)){
                            jsonObject.put("id", idProveedor)
                            jsonObject.put("notificationToken", token)
                            update()
                        }
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
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            val response = service.updateProfile(requestBody)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    Toast.makeText(requireActivity(), "Token actualizado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireActivity(), "Error al actualizar token", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(){
        val retrofit = getRetrofit()

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
                        val providerList:Appointment = gson.fromJson(provedor.toString(), Appointment::class.java)
                        lista.add(providerList)
                    }
                    adapter = AppointmentAdapter(emptyList())
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