package mx.edu.utez.deal.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Adapter.ProviderAdapter
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
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
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        getData()

        val root: View = binding.root
/*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }
    fun init(){
        var proveedores:ArrayList<Provider> = ArrayList<Provider>()
//
//        proveedores.add(Provider("Alan Fernandez","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Barbería","https://www.purina-latam.com/sites/g/files/auxxlc391/files/styles/social_share_large/public/Que_debes_saber_antes_de_adoptar_un_gatito.jpg?itok=guFplHEU", "10:21","18:30"))
//        proveedores.add(Provider("Diana","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Estetica","https://www.flowww.net/hubfs/Q1_Febrero%202020/BLOG/cual-es-la-mejor-ubicacion-para-abrir-un-centro-de-estetica_flowww.jpg", "10:21","18:30"))
//        proveedores.add(Provider("Leo","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Carpinteria","https://previews.123rf.com/images/dolgachov/dolgachov1610/dolgachov161012005/64860816-profesi%C3%B3n-carpinter%C3%ADa-ebanister%C3%ADa-y-el-concepto-de-la-gente-carpintero-con-tablones-de-madera-martil.jpg", "10:21","18:30"))
//        proveedores.add(Provider("Alan","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Plomeria","https://www.elmejorplomero.com/imagenes/fontaneros.jpg", "10:21","18:30"))
//        proveedores.add(Provider("Alan","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Veterinaria","https://www.promedco.com/images/NOTICIAS_2020/reducir-estres-de-mascotas-1.jpg", "10:21","18:30"))
//
//        val appContext = requireContext().applicationContext
//        var mi_adaptador = AdapterProvider(proveedores, appContext)
//
//        val list: ListView = binding.lista
//        list.adapter = mi_adaptador
//
//        list.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                val selectedItemText = parent.getItemAtPosition(position) as Provider
//                val intent = Intent(appContext, DetailProvider::class.java)
//                intent.putExtra("Nombre", selectedItemText.name);
//                intent.putExtra("Area", selectedItemText.area)
//                intent.putExtra("Descripcion", selectedItemText.description)
//                intent.putExtra("HoraI", selectedItemText.startTime);
//                intent.putExtra("HoraF", selectedItemText.finalTime)
//                intent.putExtra("Imagen", selectedItemText.image)
//                intent.putExtra("telefono", selectedItemText.phone)
//
//                startActivity(intent);
//                println("Selected ${selectedItemText}")
//            }
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

            val response = service.getProvider()

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
                    var lista:ArrayList<ProviderList> = ArrayList<ProviderList>()
                    while (i < Jarray.length() ){
                        var provedor:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val providerList:ProviderList = gson.fromJson(provedor.toString(), ProviderList::class.java)
                        lista.add(providerList)
                        i++
                    }
                    println("Lsita llena: ${lista.get(0).user.username}")
                    if(lista.isEmpty()){
                        Toast.makeText(activity, "No hay proveedores disponibles", Toast.LENGTH_LONG).show()
                    }else{
                        binding.rvProviders.layoutManager = LinearLayoutManager(activity)
                        val adapter = ProviderAdapter(lista)
                        binding.rvProviders.adapter=adapter
                        adapter!!.notifyDataSetChanged()
                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}