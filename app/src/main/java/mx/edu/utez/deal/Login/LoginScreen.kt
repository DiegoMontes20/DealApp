package mx.edu.utez.deal.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Registro.RegistroScreen
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.configuration.ConfIP
import mx.edu.utez.deal.databinding.ActivityLoginScreenBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //println(ConfIP.IP)
        binding.btnLogin.setOnClickListener {
            if(validar()){
                Snackbar.make(it, "Por favor llena los campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                if(login()){
                    Toast.makeText(applicationContext,
                        "Sesion exitosa", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,
                        "Valio verga", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.registrarse.setOnClickListener {
            startActivity(  Intent(this, RegistroScreen::class.java))
        }
    }
    fun login():Boolean{
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)
        // Create HashMap with fields
        val params = HashMap<String?, String?>()
        params["username"] = binding.loUser.text.toString()
        params["password"] = binding.loPassword.text.toString()
        var validar:Boolean =false
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.login(params)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    println("Headers, ${response.headers()}")
                    var auto = response.headers().get("Authorization")
                    var autho = auto?.replace(":","")
                    println("Auto, ${autho}")
                    prefs.save("token", autho.toString())
                    validar=true
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
        return validar
    }

    fun validar():Boolean{
        return binding.loUser.text.isEmpty() || binding.loPassword.text.isEmpty()
    }
}