package mx.edu.utez.deal.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemTimeBinding


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
                DetailProvider.tiempo = time
                binding.time.setTextColor(Color.parseColor("#70A7BF"))
                Toast.makeText(itemView.context, "Hora: $time", Toast.LENGTH_SHORT).show()
            }
        }
    }
}