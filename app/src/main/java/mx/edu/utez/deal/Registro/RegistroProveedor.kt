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
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityRegistroProveedorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime

class RegistroProveedor : AppCompatActivity() {
    private lateinit var binding : ActivityRegistroProveedorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backLogin.setOnClickListener {
            changeLogin()
        }

        binding.btnProRegister.setOnClickListener {
            if(check()){
                Snackbar.make(it, "Por favor llena los campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                Handler().postDelayed({

                    create(binding.reUsername.text.toString(),
                        binding.rePassword.text.toString(),
                        binding.reName.text.toString(),
                        binding.rePhone.text.toString(),
                        binding.reDescripcion.text.toString(),
                        binding.reArea.text.toString(),
                        binding.reStartTime.text.toString(),
                        binding.reFinalTime.text.toString(),)

                }, 2000)
            }
        }
    }
    fun changeLogin(){
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun check():Boolean{
        return  binding.reUsername.text.isEmpty()||
                binding.rePassword.text.isEmpty() ||
                binding.reName.text.isEmpty() ||
                binding.rePhone.text.isEmpty() ||
                binding.reDescripcion.text.isEmpty() ||
                binding.reArea.text.isEmpty() ||
                binding.reStartTime.text.isEmpty() ||
                binding.reFinalTime.text.isEmpty()
    }

    fun create(username:String,password:String, name:String,
    phone:String, description:String, area:String,
    startTime:String, finalTime:String){
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
                var user: User = User(username, password,token.toString())
                var image = "https://www.mejorinfluencer.com/wp-content/uploads/2019/06/Mym-Alkapone-Streamer.png"
                var img ="https://cdn-icons-png.flaticon.com/512/147/147144.png"
                var provider:Provider = Provider(name,phone,description, area, img, startTime, finalTime, user,null)
                println("Cliente ${provider.toString()}")
                service.createProvider(provider).enqueue(object : Callback<Void>{
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