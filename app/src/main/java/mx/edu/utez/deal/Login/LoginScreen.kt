package mx.edu.utez.deal.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login_screen.*
import mx.edu.utez.deal.MainActivity
import mx.edu.utez.deal.R
import mx.edu.utez.deal.Registro.RegistroScreen

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        registrarse.setOnClickListener {
            startActivity(  Intent(this, RegistroScreen::class.java))
        }

        ingresar.setOnClickListener {
            startActivity(  Intent(this, MainActivity::class.java))
        }
    }
}