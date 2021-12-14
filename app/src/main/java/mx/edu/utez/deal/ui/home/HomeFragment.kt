package mx.edu.utez.deal.ui.home

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.adapter.ProviderAdapter
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.ClientProfile
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.SplashScreen
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.FragmentHomeBinding
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit




class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    var idCliente =""
    var apellidos =""
    var telefono =""
    var nombre =""
    var tokenCliente =""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val jsonObject = JSONObject()
    val jsonUser = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.rvProviders.layoutManager = LinearLayoutManager(activity)
        getData("")
        getProfile()

        val root: View = binding.root

        root.img_buscar.setOnClickListener {
            getData(binding.buscarNombre.text.toString())
        }

        return root
    }

    fun update(){
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
                    Toast.makeText(activity, "Token actualizado", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(activity, "Error al actualizar token", Toast.LENGTH_LONG).show()
                }
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
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    var jobject:JSONObject = JSONObject(prettyJson)
                    val gson2 = Gson()
                    val clientProfile: ClientProfile = gson2.fromJson(jobject.get("data").toString(), ClientProfile::class.java)
                    idCliente = clientProfile.id
                    apellidos = clientProfile.lastname
                    telefono=clientProfile.phone
                    nombre=clientProfile.name
                    tokenCliente = clientProfile.token
                    var token: String? = null
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("MyFirebaseMsgService->", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        token = task.result

                        if(!token.equals(tokenCliente)){
                            jsonObject.put("id", idCliente)
                            jsonObject.put("name", nombre)
                            jsonObject.put("lastname", apellidos)
                            jsonObject.put("phone", telefono)

                            jsonUser.put("notificationToken",token)
                            jsonObject.put("user", jsonUser)
                            update()
                        }
                    })
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


    fun getData(nombre:String){
        val retrofit = getRetrofit()

        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler) {

            val response = service.getProvidersByName(nombre)

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
                    var Jarray:JSONArray = jobject.getJSONArray("data")
                    var i=0
                    var lista:ArrayList<ProviderList> = ArrayList()

                    while (i < Jarray.length() ){
                        var provedor:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val providerList:ProviderList = gson.fromJson(provedor.toString(), ProviderList::class.java)
                        lista.add(providerList)
                        i++
                    }
                    if(lista.isEmpty()){
                        binding.btnTexto.visibility = View.VISIBLE
                        Toast.makeText(activity, "No hay proveedores disponibles", Toast.LENGTH_LONG).show()
                        val adapter = ProviderAdapter(lista)
                        binding.rvProviders.adapter=adapter
                        adapter!!.notifyDataSetChanged()
                    }else{
                        binding.btnTexto.visibility = View.GONE

                        val adapter = ProviderAdapter(lista)
                        binding.rvProviders.adapter=adapter
                        adapter!!.notifyDataSetChanged()
                    }
                    hideKeyboard()
                } else {
                    var code = response.code().toString()
                    if(code == "401"){
                        Toast.makeText(activity, "La sesión ha expirado", Toast.LENGTH_LONG).show()
                        prefs.deleteAll()
                        val intent = Intent(activity, LoginScreen::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }


    }
    private fun hideKeyboard() {
        binding.buscarNombre.clearFocus()
        val imm = view?.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
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