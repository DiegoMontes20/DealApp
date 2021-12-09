package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import mx.edu.utez.deal.databinding.ActivityCitaConfirmacionBinding
import mx.edu.utez.deal.proveedor.mapa.MapaActivity

class CitaConfirmacion : AppCompatActivity() {
    private lateinit var binding: ActivityCitaConfirmacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaConfirmacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCita = getIntent().getStringExtra("idCita");
        val nombreCliente = getIntent().getStringExtra("nombreCliente");
        val numeroCliente = getIntent().getStringExtra("telefonoCliente");
        val fechaHora = getIntent().getStringExtra("fechaHora");
        val ubicacion = getIntent().getStringExtra("ubicacion");

        binding.infoFechaHora.text = "${fechaHora?.substring(0,10)} Hora: ${fechaHora?.substring(11,16)}"
        binding.infoCliente.text = nombreCliente
        binding.infoPhone.text = numeroCliente
        binding.address.text=ubicacion

        binding.btnCancelarCita.setOnClickListener {
            startActivity(Intent(this, PendienteList::class.java))

        }

        binding.btnConfirmarCita.setOnClickListener {
            Toast.makeText(this, "Confirmando cita: " + idCita, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MapaActivity::class.java))
        }

    }
}