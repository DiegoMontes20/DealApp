package mx.edu.utez.deal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.chat2.ChatActivity
import mx.edu.utez.deal.configuration.ConfIP
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println("Se ejecuta el servicio")
        if (remoteMessage.data.isEmpty()){
            println("Mensaje -> ${remoteMessage.data}")
//            if(ChatActivity.chatActivo){
//                enviarNotificacionLocal(remoteMessage.data.get("mensaje").toString())
//                saveData(remoteMessage.data.get("mensaje").toString())
//            }else{
//                enviarNotificacionLocal(remoteMessage.data.get("mensaje").toString())
//                showNotification(remoteMessage.data.get("mensaje").toString())
//                saveData(remoteMessage.data.get("mensaje").toString())
//            }

        }

        remoteMessage.notification?.let {
            println("Firebase notification -> ${it.body.toString()}")
            //showNotification(it.title.toString())
        }
    }

    fun saveData(mensaje:String){
        val retrofit = getRetrofit()

        //OBJETO PARA MANDAR
        val objEnviar = JSONObject()

        // JSON id Provider
        val idProvider = JSONObject()
        idProvider.put("id", ChatActivity.idProveedor)

        //JSON MESSAGE
        val messageBody = JSONObject()
        messageBody.put("body", mensaje)


        if(!ChatActivity.listaVacia){
            //Primer mensaje
            objEnviar.put("provider", idProvider)
            objEnviar.put("message",messageBody)
        }else{
            //recuperar el id de la conversacion
            val idConversa = JSONObject()
            idConversa.put("id", ChatActivity.idConversacion)

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
                    //getMessages()
                }else{
                    println(response.code().toString())
                }
            }
        }
    }

    fun enviarNotificacionLocal(mensaje: String){
        val FILTRO_CHAT ="broadcast_chat"
        val intent = Intent(FILTRO_CHAT)
        intent.putExtra("mensaje", mensaje)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun getRetrofit(): Retrofit {
        return  Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .client(OkHttpClient.Builder().addInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", PrefsApplication.prefs.getData("token")).build()
                chain.proceed(request)
            }.build())
            .build()
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        sendRegistration(newToken)
    }

    fun sendRegistration(token:String){
        println("Enviando token al web service -> ${token}")
    }

    fun showNotification(mensaje:String){
        println("ShowNotification")
        val intent = Intent(this, ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelID= getString(R.string.app_name)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificacionBuilder = NotificationCompat.Builder(this,channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("UTEZ")
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID, "title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificacionBuilder.build())
        //notificationManager.createNotificationChannel(notificacionBuilder)

    }
}