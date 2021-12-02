package mx.edu.utez.deal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemproveedorBinding

class AppointmetAdapter(val appointments:List<AppointmentModel>): RecyclerView.Adapter<AppointmetAdapter.AppointmentHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmetAdapter.AppointmentHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AppointmetAdapter.AppointmentHolder(
            layoutInflater.inflate(
                R.layout.itemproveedor,
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
        val binding= ItemproveedorBinding.bind(view)
        fun render(appointment:AppointmentModel){
            binding.info.text = "${appointment.provider.area}. \n ${appointment.provider.description}"
            binding.tipoServicio.text = appointment.provider.name
            binding.horario.text ="${appointment.provider.startTime.substring(0,5)} a ${appointment.provider.finalTime.substring(0,5)}"
            binding.numero.text = appointment.provider.phone
            Picasso.get().load(appointment.provider.image).into(binding.imgProfile);
            view.setOnClickListener {
                Toast.makeText(view.context,"${appointment.dateTime}", Toast.LENGTH_LONG).show()
            }
        }
    }
}