package mx.edu.utez.deal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.CitaItemBinding
import mx.edu.utez.deal.proveedor.agenda.CitaConfirmacion
import mx.edu.utez.deal.proveedor.agenda.CitaDetails
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class AppointmentAdapter(var citas: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ProviderHolder>() {

    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProviderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProviderHolder(layoutInflater.inflate(R.layout.cita_item, parent, false))
    }

    override fun onBindViewHolder(holder: ProviderHolder, position: Int) {
        holder.render(citas[position])

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
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
            }
        }
        fun clearAnimation() {
            view.clearAnimation()
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    //clear animation if it was already displayed
    override fun onViewDetachedFromWindow(holder: ProviderHolder) {
        holder.clearAnimation()
    }
}