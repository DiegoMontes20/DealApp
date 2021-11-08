package mx.edu.utez.deal.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login_screen.*
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Registro.RegistroScreen
import mx.edu.utez.deal.configuration.ConfIP

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        println(ConfIP.IP)

        registrarse.setOnClickListener {
            startActivity(  Intent(this, RegistroScreen::class.java))
        }
    }
}