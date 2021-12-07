package mx.edu.utez.deal.proveedor.perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityEditProveedorBinding

class EditProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityEditProveedorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveInfo.setOnClickListener {
            Toast.makeText(this, "Guardando info", Toast.LENGTH_LONG).show()

        }
    }
}