package mx.edu.utez.deal.proveedor.agenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.databinding.ActivityPendienteListBinding

class PendienteList : AppCompatActivity() {
    private lateinit var binding: ActivityPendienteListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendienteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listaPendientes.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position) as Appointment
                val intent = Intent(applicationContext, CitaConfirmacion::class.java)
                intent.putExtra("idCita", selectedItemText.client.id);
                intent.putExtra("nombreCliente", selectedItemText.client.fullname);
                intent.putExtra("numeroCliente", selectedItemText.client.phone);
                intent.putExtra("dateTime", selectedItemText.dateTime)
                startActivity(intent);
            }


    }
}