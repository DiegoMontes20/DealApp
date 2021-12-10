package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.edu.utez.deal.databinding.ActivityCitaDetailsBinding
import mx.edu.utez.deal.proveedor.mapa.MapaActivity

class CitaDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCitaDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreCliente = intent.getStringExtra("nombreCliente");
        val numeroCliente = intent.getStringExtra("telefonoCliente");
        val fechaHora = intent.getStringExtra("dateTime");
        val locationName = intent.getStringExtra("locationName");
        val locationLatitude = intent.getDoubleExtra("locationLatitude", 0.0);
        val locationLongitude = intent.getDoubleExtra("locationLongitude", 0.0);

        binding.txtFechaHora.text = fechaHora
        binding.txtCliente.text = nombreCliente
        binding.txtPhone.text = numeroCliente

        if (locationName.isNullOrEmpty())
            binding.btnMostrarUbicacion.visibility = View.GONE

        binding.btnMostrarUbicacion.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("locationName", locationName)
            intent.putExtra("locationLatitude", locationLatitude)
            intent.putExtra("locationLongitude", locationLongitude)
            startActivity(intent)
        }

        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }
}