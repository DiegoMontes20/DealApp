package mx.edu.utez.deal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mx.edu.utez.deal.Model.AppointmentModel
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemTimeBinding
import mx.edu.utez.deal.databinding.ItemproveedorBinding

class ItemTimeAdapter(var times: List<String>) :
    RecyclerView.Adapter<ItemTimeAdapter.ITemTimeHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ITemTimeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ITemTimeHolder(
            layoutInflater.inflate(
                R.layout.item_time,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ITemTimeHolder, position: Int) {
        holder.render(times[position])
    }

    override fun getItemCount(): Int {
        return times.size
    }

    class ITemTimeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTimeBinding.bind(view)
        fun render(time: String) {
            binding.time.text = time
            binding.time.setOnClickListener {
                Toast.makeText(itemView.context, "Hora: $time", Toast.LENGTH_SHORT).show()
            }
        }
    }
}