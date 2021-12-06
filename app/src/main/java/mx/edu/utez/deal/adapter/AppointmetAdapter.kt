package mx.edu.utez.deal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemcitasBinding
import mx.edu.utez.deal.databinding.ItemproveedorBinding

class AppointmetAdapter(val appointments:List<AppointmentModel>): RecyclerView.Adapter<AppointmetAdapter.AppointmentHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmetAdapter.AppointmentHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AppointmetAdapter.AppointmentHolder(
            layoutInflater.inflate(
                R.layout.itemcitas,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppointmetAdapter.AppointmentHolder, position: Int) {
        holder.render(appointments[position])
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    class AppointmentHolder(val view: View):RecyclerView.ViewHolder(view){
        val binding = ItemcitasBinding.bind(view)

        fun render(appointment:AppointmentModel){

           binding.horario.text="${appointment.dateTime.substring(0,10)} ${appointment.provider.startTime.substring(0,5)} a ${appointment.provider.finalTime.substring(0,5)}"
            binding.nombreProveedor.text = "${appointment.provider.name} (${appointment.provider.area})"
            view.setOnClickListener {
                val intent = Intent(view.context, DetailProvider::class.java)
                intent.putExtra("id", appointment.provider.id);
                intent.putExtra("Nombre", appointment.provider.name);
                intent.putExtra("Area", appointment.provider.area)
                intent.putExtra("Descripcion", appointment.provider.description)
                intent.putExtra("HoraI", appointment.provider.startTime);
                intent.putExtra("HoraF", appointment.provider.finalTime)
                intent.putExtra("Imagen", appointment.provider.image)
                intent.putExtra("telefono", appointment.provider.phone)
                DetailProvider.chat=true
                view.context.startActivity(intent)
            }
        }
    }
}