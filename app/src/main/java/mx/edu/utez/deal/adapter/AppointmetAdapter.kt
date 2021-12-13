package mx.edu.utez.deal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.chat2.AgendaSummary
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemcitasBinding

class AppointmetAdapter(var appointments:List<AppointmentModel>): RecyclerView.Adapter<AppointmetAdapter.AppointmentHolder>() {

    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AppointmetAdapter.AppointmentHolder(
            layoutInflater.inflate(
                R.layout.itemcitas,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppointmentHolder, position: Int) {
        holder.render(appointments[position])
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    class AppointmentHolder(val view: View):RecyclerView.ViewHolder(view){
        val binding = ItemcitasBinding.bind(view)

        fun render(appointment:AppointmentModel){

           binding.horario.text="${appointment.dateTime.substring(0,10)} ${appointment.dateTime.substring(11,16)}"
            binding.nombreProveedor.text = "${appointment.provider.name} (${appointment.provider.area})"
            view.setOnClickListener {
                val intent = Intent(view.context, AgendaSummary::class.java)
                intent.putExtra("idCita", appointment.id);
                intent.putExtra("idProveedor", appointment.provider.id);

                intent.putExtra("fechaHora", appointment.dateTime);
                intent.putExtra("descripcion",appointment.provider.description)
                intent.putExtra("nombreProveedor",appointment.provider.name)
                intent.putExtra("tipoServicio",appointment.provider.area)

                intent.putExtra("ubicacion",appointment.location?.name)
                intent.putExtra("latitude",appointment.location?.latitude)
                intent.putExtra("longitude",appointment.location?.longitude)
                intent.putExtra("enabled",appointment.enabled)
                intent.putExtra("approved",appointment.approved)
                intent.putExtra("onWay",appointment.onWay)
                DetailProvider.chat=true
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
    override fun onViewDetachedFromWindow(holder: AppointmentHolder) {
        holder.clearAnimation()
    }
}