package mx.edu.utez.deal.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.MainActivity
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Registro.RegistroProveedor
import mx.edu.utez.deal.Registro.RegistroScreen
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityLoginScreenBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginScreen : AppCompatActivity() {
    private lateinit var  binding : ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_login_screen)

        binding.registrarse.setOnClickListener {
            changeRegister()
        }

        binding.btnLogin.setOnClickListener {
            if(validar()){
                Snackbar.make(it, "Por favor llena los campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                println("status login ${login()}")
//                if(login()){
//                    Toast.makeText(applicationContext,
//                        "Sesion exitosa", Toast.LENGTH_LONG).show()
//                    changeMain()
//                }else{
//                    Toast.makeText(applicationContext,
//                        "Valió verga", Toast.LENGTH_LONG).show()
//                }
            }
        }

    }

    fun validar():Boolean{
        return binding.loUser.text.isEmpty() || binding.loPassword.text.isEmpty()
    }

    fun login(){
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create Service
        val service = retrofit.create(APIService::class.java)

        val user:User = User(binding.loUser.text.toString(),binding.loPassword.text.toString(),"123" )
        var validar:Boolean =false
        service.login(user).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    println("Headers, ${response.headers()}")
                    var auto = response.headers().get("Authorization")
                    var autho = auto?.replace(":","")
                    println("Auto, ${autho}")
                    prefs.save("token", autho.toString())
                    validar=true
                    Toast.makeText(applicationContext, "Bienenid@ ${binding.loUser.text.toString()}", Toast.LENGTH_LONG).show()
                    changeMain()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al iniciar", Toast.LENGTH_LONG).show()
            }
        })


    }


    fun changeRegister(){
        val intent = Intent(this, RegistroProveedor::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
    fun changeMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}