package mx.edu.utez.deal.proveedor.perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.databinding.ActivityMenuPerfilBinding
import mx.edu.utez.deal.proveedor.agenda.AgendaList

class MenuPerfil : AppCompatActivity() {
    private lateinit var binding: ActivityMenuPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDatosGenerales.setOnClickListener {
            startActivity(Intent(this, PerfilProveedor::class.java))

        }

        binding.btnHorarioLaboral.setOnClickListener {
            startActivity(Intent(this, AgendaList::class.java))

        }

        binding.btnCerrarSesion.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))

        }

    }
}