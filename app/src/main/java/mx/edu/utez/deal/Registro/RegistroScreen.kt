package mx.edu.utez.deal.Registro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registro_screen.*
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.R

class RegistroScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_screen)

        volverIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
        }
    }
}