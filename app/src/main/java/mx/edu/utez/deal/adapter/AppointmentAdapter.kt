package mx.edu.utez.deal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.CitaItemBinding
import mx.edu.utez.deal.proveedor.agenda.CitaConfirmacion
import mx.edu.utez.deal.proveedor.agenda.CitaDetails

class AppointmentAdapter(var citas: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ProviderHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentAdapter.ProviderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProviderHolder(layoutInflater.inflate(R.layout.cita_item, parent, false))
    }

    override fun onBindViewHolder(holder: AppointmentAdapter.ProviderHolder, position: Int) {
        holder.render(citas[position])
    }

    override fun getItemCount(): Int {
        return citas.size
    }

    class ProviderHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = CitaItemBinding.bind(view)
        fun render(cita: Appointment) {
            binding.fecha.text =
                "Fecha: ${cita.dateTime.substring(0, 10)} Hora: ${cita.dateTime.substring(11, 16)}"
            binding.nombre.text = cita.client.fullname
            view.setOnClickListener {
                val intent = Intent(
                    view.context,
                    if (cita.approved.not() && cita.enabled) CitaConfirmacion::class.java else CitaDetails::class.java
                )
                intent.putExtra("idCita", cita.id)
                intent.putExtra("nombreCliente", cita.client.fullname)
                intent.putExtra("telefonoCliente", cita.client.phone)
                intent.putExtra("dateTime", cita.dateTime)
                intent.putExtra("estado", cita.enabled)
                if (cita.location != null) {
                    intent.putExtra("locationName", cita.location.name)
                    intent.putExtra("locationLatitude", cita.location.latitude)
                    intent.putExtra("locationLongitude", cita.location.longitude)
                }
                view.context.startActivity(intent)
                //Toast.makeText(view.context, cita.dateTime, Toast.LENGTH_LONG).show()
            }
        }
    }
}