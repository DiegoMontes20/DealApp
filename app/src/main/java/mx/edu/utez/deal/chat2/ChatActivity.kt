package mx.edu.utez.deal.chat2

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
import kotlinx.coroutines.*
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.Model.chat.Message
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.adapterChat.AdapterChat
import mx.edu.utez.deal.adapterChat.ModelChat
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityChatBinding
import mx.edu.utez.deal.utils.coroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class ChatActivity : AppCompatActivity() {


    private lateinit var binding :ActivityChatBinding

    private lateinit var myJob: Job

    var mensajes_chat:ArrayList<Conversation> = ArrayList<Conversation>()
    private lateinit var messageAdapter: AdapterChat


    companion object{
        var idProveedor =""
        var idConversacion =""
        var listaVacia=false
    }

    private lateinit var jobject: JSONObject
    private lateinit var  Jarray: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        messageAdapter = AdapterChat(this)


        val parametros = this.intent.extras
        idProveedor = parametros!!.getString("idProvider").toString()


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
        myJob = startRepeatingJob(3000L)

        /*los estilos de cada burbuja son los que se llaman redondeo y redondeo2*/
    }


    fun sendMessage(mensaje: String, tipo:String){
        messageAdapter.add(ModelChat(mensaje, tipo))
        messageAdapter.notifyDataSetChanged()
        binding.mensajeChat.setText("")
        binding.listaChat.setSelection(messageAdapter.count-1)
    }

    fun saveData(mensaje:String){

        val retrofit = getRetrofit()

        //OBJETO PARA MANDAR
        val objEnviar = JSONObject()

        // JSON id Provider
        val idProvider = JSONObject()
        idProvider.put("id", idProveedor)

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
        //println(objEnviar)
        // Convert JSONObject to String
        val jsonObjectString = objEnviar.toString()
        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            val response = service.saveMessage(requestBody)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    //Toast.makeText(applicationContext, "se envio el mensaje", Toast.LENGTH_SHORT).show()
                    //getMessages()
                    sendMessage(mensaje, "Client")
                }else{
                    Log.e("Error", response.code().toString())
                }
            }
        }
    }

    fun getMessages(){
        val retrofit =getRetrofit()
        val service = retrofit.create(APIService::class.java)
        jobject = JSONObject()
        Jarray = JSONArray()
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler.handler){
            val response = service.getMessages()
            runOnUiThread {
                if(response.isSuccessful){
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    //Log.w("response", prettyJson)
                    jobject = JSONObject(prettyJson)
                    Jarray= jobject.getJSONArray("data")
                    mensajes_chat.clear()
                    for(i in 0 until Jarray.length()){
                        var conversationObj:JSONObject = Jarray.getJSONObject(i)
                        val gson = Gson()
                        val conversation:Conversation = gson.fromJson(conversationObj.toString(), Conversation::class.java)
                        if(conversation.provider.id.equals(idProveedor)){
                            mensajes_chat.add(conversation)
                            idConversacion = conversation.id
                        }
                    }
                    if(mensajes_chat.isNotEmpty()){
                        listaVacia=true
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
        return CoroutineScope(Dispatchers.Main).launch(coroutineExceptionHandler.handler) {
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