package mx.edu.utez.deal.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapterChat.AdapterConversation
import mx.edu.utez.deal.databinding.FragmentChatBinding
import mx.edu.utez.deal.ui.home.HomeFragment
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private lateinit var conversationAdapter: AdapterConversation

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getConversations()

        binding.btnMarca.text = HomeFragment.nombreEmpresa
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun getConversations(){
        val retrofit =getRetrofit()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.getMessages()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    var jobject: JSONObject = JSONObject(prettyJson)
                    var Jarray: JSONArray = jobject.getJSONArray("data")
                    val conversations: ArrayList<Conversation> = ArrayList()
                    for(i in 0 until Jarray.length()){
                        var conversationObj:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val conversation:Conversation = gson.fromJson(conversationObj.toString(), Conversation::class.java)
                        conversations.add(conversation)
                    }
                    //println(conversations)
                    conversationAdapter = AdapterConversation(requireActivity())
                    conversationAdapter.conversations = conversations
                    _binding!!.listaConversation.adapter = conversationAdapter
                    conversationAdapter.notifyDataSetChanged()
                    if (conversations.isEmpty()) {
                        _binding!!.titleSinConversaciones.visibility = View.VISIBLE
                        _binding!!.listaConversation.visibility = View.GONE
                    } else {
                        _binding!!.titleSinConversaciones.visibility = View.GONE
                        _binding!!.listaConversation.visibility = View.VISIBLE
                    }
                }else{
                    Toast.makeText(activity, "Error en la consulta :c", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getRetrofit():Retrofit {
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