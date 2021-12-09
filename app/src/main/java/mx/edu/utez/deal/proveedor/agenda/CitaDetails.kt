package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.databinding.ActivityCitaDetailsBinding

class CitaDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCitaDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreCliente = getIntent().getStringExtra("nombreCliente");
        val numeroCliente = getIntent().getStringExtra("numeroCliente");
        val fechaHora = getIntent().getStringExtra("dateTime");

        binding.txtFechaHora.setText(fechaHora)
        binding.txtCliente.setText(nombreCliente)
        binding.txtPhone.setText(numeroCliente)

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this, AgendaList::class.java))
        }

    }
}