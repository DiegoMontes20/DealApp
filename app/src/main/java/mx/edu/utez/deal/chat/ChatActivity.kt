package mx.edu.utez.deal.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityChatBinding
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class ChatActivity : AppCompatActivity() {
    private lateinit var binding :ActivityChatBinding
    var id=""
    // var mensajes_chat:ArrayList<ChatModel> = ArrayList<ChatModel>()
    var mensajes_chat:ArrayList<Conversation> = ArrayList<Conversation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val parametros = this.intent.extras
        id= parametros!!.getString("id").toString()
        binding.btnMarca.text = parametros!!.getString("nombre")
        binding.regresar.setOnClickListener {
            onBackPressed()
        }

        getMessages()




        /*los estilos de cada burbuja son los que se llaman redondeo y redondeo2*/
    }

    fun getMessages(){
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
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
                    var i=0
                    while (i < Jarray.length() ){
                        var conversationObj:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val conversation:Conversation = gson.fromJson(conversationObj.toString(), Conversation::class.java)
                        if(conversation.provider.id.equals(id)){
                            mensajes_chat.add(conversation)
                        }
                        i++
                    }

                }
            }
        }
    }
}