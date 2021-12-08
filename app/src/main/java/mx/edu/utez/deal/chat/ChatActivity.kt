package mx.edu.utez.deal.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class ChatActivity : AppCompatActivity() {
    private lateinit var binding :ActivityChatBinding
    var id=""
    var idConversacion = ""
    var mensajes_chat:ArrayList<Conversation> = ArrayList<Conversation>()
    var mensajes_chat_modal:ArrayList<ChatModel> = ArrayList<ChatModel>()


    lateinit var messageAdapter:ChatAdapter

    private val FILTRO_CHAT ="broadcast_chat"

    var flag = true

    companion object{
        var chatActivo = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageAdapter = ChatAdapter(this)
        binding.listaChat.adapter =messageAdapter

        chatActivo=true

        val parametros = this.intent.extras
        id= parametros!!.getString("idProvider").toString()
        binding.btnMarca.text = parametros!!.getString("nombre")
        binding.regresar.setOnClickListener {
            onBackPressed()
        }

        binding.enviaMensaje.setOnClickListener {
            var mensa= binding.mensajeChat.text.toString()
            if(mensa.isEmpty()){
                Toast.makeText(this, "Llena el campo", Toast.LENGTH_SHORT).show()
            }else{
                saveData(mensa)
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast, IntentFilter(FILTRO_CHAT))
        getMessages()




        /*los estilos de cada burbuja son los que se llaman redondeo y redondeo2*/
    }
    val broadcast = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            saveData(intent!!.getStringExtra("mensaje").toString())
        }
    }



    fun saveData(mensaje:String){
        val retrofit = getRetrofit()

        //OBJETO PARA MANDAR
        val objEnviar = JSONObject()

        // JSON id Provider
        val idProvider = JSONObject()
        idProvider.put("id", id)

        //JSON MESSAGE
        val messageBody = JSONObject()
        messageBody.put("body", mensaje)


        if(mensajes_chat.isEmpty()){
            //Primer mensaje
            objEnviar.put("provider", idProvider)
            objEnviar.put("message",messageBody)
        }else{
            //recuperar el id de la conversacion
            val idConversa = JSONObject()
            idConversa.put("id", idConversacion)

            objEnviar.put("conversation", idConversa)
            objEnviar.put("provider", idProvider)
            objEnviar.put("message",messageBody)
        }
        println(objEnviar)
        // Convert JSONObject to String
        val jsonObjectString = objEnviar.toString()
        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.saveMessage(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    binding.mensajeChat.setText("")
                    getMessages()
                }else{
                    println(response.code().toString())
                }
            }
        }
    }


    fun getMessages(){
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
                    var i=0
                    mensajes_chat.clear()
                    mensajes_chat_modal.clear()
                    while (i < Jarray.length() ){
                        var conversationObj:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val conversation:Conversation = gson.fromJson(conversationObj.toString(), Conversation::class.java)
                        if(conversation.provider.id.equals(id)){
                            //messageAdapter.add(conversation)
                            mensajes_chat.add(conversation)
                            idConversacion = conversation.id
                        }
                        i++
                    }
                    if(mensajes_chat.isNotEmpty()){
                        messageAdapter.clear()
                        for(position in mensajes_chat.get(0).messages.indices){
                            messageAdapter.add( ChatModel(mensajes_chat.get(0).messages.get(position).body, mensajes_chat.get(0).messages.get(position).sender.role))
                        }
                        messageAdapter.notifyDataSetChanged()
                    }
                    //binding.listaChat.setSelection(messageAdapter.count-1)
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
    override fun onStart() {
        super.onStart()
        chatActivo=true
    }

    override fun onStop() {
        super.onStop()
        chatActivo=false
    }
}