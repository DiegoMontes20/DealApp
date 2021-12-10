package mx.edu.utez.deal.proveedor.chat

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
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapterChat.AdapterChat
import mx.edu.utez.deal.adapterChat.ModelChat
import mx.edu.utez.deal.databinding.ActivityChatProveedorBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class ChatProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityChatProveedorBinding
    private lateinit var messageAdapter: AdapterChat
    private val FILTRO_CHAT ="broadcast_chat"
    private lateinit var conversation: Conversation

    companion object{
        var chatActivo = false
        var idProveedor =""
        var idConversacion =""
        var listaVacia=false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationJson = intent.getStringExtra("conversation")
        val gson = Gson()
        conversation = gson.fromJson(conversationJson, Conversation::class.java)

        messageAdapter = AdapterChat(this)
        binding.listaChat.adapter = messageAdapter
        val listMessages = ArrayList<ModelChat>()
        conversation.messages.forEach { message -> listMessages.add(ModelChat(message.body, message.sender.role)) }
        messageAdapter.mensajes = listMessages

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
    }

    fun sendMessage(mensaje: String, tipo:String){
        messageAdapter.add(ModelChat(mensaje, tipo))
        binding.mensajeChat.setText("")
        binding.listaChat.setSelection(messageAdapter.count-1)
    }



    fun saveData(mensaje:String){

        val retrofit = getRetrofit()

        //OBJETO PARA MANDAR
        val objEnviar = JSONObject()

        //JSON MESSAGE
        val messageBody = JSONObject()
        messageBody.put("body", mensaje)


        //recuperar el id de la conversacion
        val idConversa = JSONObject()
        idConversa.put("id", conversation.id)

        objEnviar.put("conversation", idConversa)
        objEnviar.put("message", messageBody)
        //println(objEnviar)
        // Convert JSONObject to String
        val jsonObjectString = objEnviar.toString()
        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            val response = service.saveMessage(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    sendMessage(mensaje, "Provider")
                }else{
                    Log.e("Error", response.code().toString())
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
        chatActivo =true
    }

    override fun onStop() {
        super.onStop()
        chatActivo =false
    }
}