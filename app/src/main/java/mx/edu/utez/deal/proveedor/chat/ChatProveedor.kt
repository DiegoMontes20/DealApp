package mx.edu.utez.deal.proveedor.chat


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapterChat.AdapterChat
import mx.edu.utez.deal.adapterChat.ModelChat
import mx.edu.utez.deal.databinding.ActivityChatProveedorBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class ChatProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityChatProveedorBinding
    private lateinit var messageAdapter: AdapterChat
    private val FILTRO_CHAT ="broadcast_chat"
    private lateinit var conversation: Conversation
    var mensajes_chat:ArrayList<Conversation> = ArrayList<Conversation>()

    private lateinit var myJob: Job

    companion object{
        var chatActivo = true
        var idConver =""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationJson = intent.getStringExtra("conversation")
        val gson = Gson()
        conversation = gson.fromJson(conversationJson, Conversation::class.java)
        idConver=conversation.id
        messageAdapter = AdapterChat(this)

        if(chatActivo){
            iniciar()
        }


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
        myJob = startRepeatingJob(3000L)
    }

    fun iniciar(){
        messageAdapter.clear()
        binding.listaChat.adapter = messageAdapter
        val listMessages = ArrayList<ModelChat>()
        conversation.messages.forEach { message -> listMessages.add(ModelChat(message.body, message.sender.role)) }
        messageAdapter.mensajes = listMessages
        messageAdapter.notifyDataSetChanged()
        binding.listaChat.setSelection(messageAdapter.count-1)
        chatActivo=false
    }

    fun sendMessage(mensaje: String, tipo:String){
        messageAdapter.add(ModelChat(mensaje, tipo))
        binding.mensajeChat.setText("")
        binding.listaChat.setSelection(messageAdapter.count-1)
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
                    var jobject = JSONObject(prettyJson)
                    var Jarray= jobject.getJSONArray("data")
                    mensajes_chat.clear()
                    for(i in 0 until Jarray.length()){
                        var conversationObj:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val conversation:Conversation = gson.fromJson(conversationObj.toString(), Conversation::class.java)
                        if(conversation.id.equals(idConver)){

                            mensajes_chat.add(conversation)
                        }
                    }
                    if(mensajes_chat.isNotEmpty()){
                        llenarAdapter(mensajes_chat)
                    }

                }
            }
        }
    }

    fun llenarAdapter(lista:ArrayList<Conversation>){
        messageAdapter.clear()
        var mensajes = lista.get(0).messages
        for(item in mensajes){
            messageAdapter.add(ModelChat(item.body, item.sender.role))
        }
        binding.listaChat.adapter = messageAdapter
        messageAdapter.notifyDataSetChanged()
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

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                getMessages()
                delay(timeInterval)
            }
        }
    }
    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
        myJob .cancel()
    }
}