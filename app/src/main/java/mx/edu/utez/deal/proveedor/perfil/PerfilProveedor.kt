package mx.edu.utez.deal.proveedor.perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityPerfilProveedorBinding

class PerfilProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilProveedorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCambiarInfo.setOnClickListener {
            startActivity(Intent(this, EditProveedor::class.java))

        }

    }
}