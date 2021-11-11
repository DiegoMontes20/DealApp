package mx.edu.utez.deal.Registro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_registro_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.Client
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityRegistroScreenBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.typeOf


class RegistroScreen : AppCompatActivity() {
    private lateinit var binding : ActivityRegistroScreenBinding
    lateinit var tokenFun: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.volverIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
        }

        binding.btnRegister.setOnClickListener {
            if(validar()){
                Snackbar.make(it, "Por favor llena los campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                Handler().postDelayed({

                    create(binding.reUsuario.text.toString(),
                    binding.reContrasenia.text.toString(),
                        binding.reNombre.text.toString(),
                    binding.reTelefono.text.toString(),
                    binding.reApellidos.text.toString())

                }, 2000)

            }
        }

    }

    fun validar():Boolean{
        return binding.reTelefono.text.isEmpty() ||
                binding.reNombre.text.isEmpty() ||
                binding.reApellidos.text.isEmpty()  ||
                binding.reUsuario.text.isEmpty() ||
                binding.reContrasenia.text.isEmpty() ||
                binding.reConfirmar.text.isEmpty()
    }

    fun create(username:String, password:String,
    name:String, phone:String, lastname:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)


        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if(!it.isSuccessful){
                println("Error en firebase ${it.exception}")
                return@OnCompleteListener
            }else{
                val token = it.result
                Log.i("SI hay","${token}")
                var user:User = User(username, password,token.toString())
                var cliente:Client = Client(name, phone,lastname,user)
                println("Cliente ${cliente.toString()}")
                service.createEmployee(cliente).enqueue(object : Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful){
                            Toast.makeText(applicationContext,
                                "Usuarios registrado", Toast.LENGTH_LONG).show()
                            changeActivity()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error al registrar", Toast.LENGTH_LONG).show()
                    }
                })

            }
        })

    }


    fun changeActivity(){
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}