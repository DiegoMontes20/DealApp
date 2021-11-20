package mx.edu.utez.deal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initApp()
    }

    private fun initApp(){
        Handler().postDelayed({
            verifyAuth()
        }, 4000)

    }

    fun obtenerToken(){
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if(!it.isSuccessful){
                println("Error en firebase ${it.exception}")
                return@OnCompleteListener
            }else{
                val token = it.result
                Log.i("Token","${token}")
                println("Firebase token ${token}")
            }
        })
    }
    fun verifyAuth(){
        if(prefs.getData("token").isNotEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }else{
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }


}