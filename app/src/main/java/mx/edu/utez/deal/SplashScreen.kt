package mx.edu.utez.deal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import mx.edu.utez.deal.Login.LoginScreen

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initApp()
    }

    private fun initApp(){
        Handler().postDelayed({
            startActivity(Intent(this, LoginScreen::class.java))
        }, 4000)

    }
}