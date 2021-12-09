package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.adapter.AppointmentAdapter
import mx.edu.utez.deal.databinding.ActivityAgendaListBinding

class AgendaList : AppCompatActivity() {
    private lateinit var binding: ActivityAgendaListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listaAgenda.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position) as Appointment
                val intent = Intent(applicationContext, CitaDetails::class.java)
                intent.putExtra("nombreCliente", selectedItemText.client.fullname);
                intent.putExtra("numeroCliente", selectedItemText.client.phone);
                intent.putExtra("dateTime", selectedItemText.dateTime)
                startActivity(intent);
            }

        binding.btnCitasPendientes.setOnClickListener {
            startActivity(Intent(this, PendienteList::class.java))
        }

    }

}