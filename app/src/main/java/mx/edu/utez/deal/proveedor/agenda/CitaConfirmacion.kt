package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import mx.edu.utez.deal.databinding.ActivityCitaConfirmacionBinding

class CitaConfirmacion : AppCompatActivity() {
    private lateinit var binding : ActivityCitaConfirmacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaConfirmacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCita = getIntent().getStringExtra("idCita");
        val nombreCliente = getIntent().getStringExtra("nombreCliente");
        val numeroCliente = getIntent().getStringExtra("numeroCliente");
        val fechaHora = getIntent().getStringExtra("dateTime");

        binding.infoFechaHora.setText(fechaHora)
        binding.infoCliente.setText(nombreCliente)
        binding.infoPhone.setText(numeroCliente)

        binding.btnCancelarCita.setOnClickListener {
            startActivity(Intent(this, PendienteList::class.java))

        }

        binding.btnConfirmarCita.setOnClickListener {
            Toast.makeText(this, "Confirmando cita: "+idCita, Toast.LENGTH_LONG).show()

        }

    }
}