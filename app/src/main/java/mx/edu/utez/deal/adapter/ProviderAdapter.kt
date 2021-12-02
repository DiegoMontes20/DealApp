package mx.edu.utez.deal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemproveedorBinding

class ProviderAdapter(val providers:List<ProviderList>):RecyclerView.Adapter<ProviderAdapter.ProviderHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProviderAdapter.ProviderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProviderHolder(layoutInflater.inflate(R.layout.itemproveedor, parent, false))
    }

    override fun onBindViewHolder(holder: ProviderAdapter.ProviderHolder, position: Int) {
        holder.render(providers[position])
    }

    override fun getItemCount(): Int {
        return providers.size
    }

    class ProviderHolder(val view:View):RecyclerView.ViewHolder(view){
        val binding= ItemproveedorBinding.bind(view)
        fun render(provider:ProviderList){
            binding.info.text = "${provider.area}. \n ${provider.description}"
            binding.tipoServicio.text = provider.name
            binding.horario.text ="${provider.startTime.substring(0,5)} a ${provider.finalTime.substring(0,5)}"
            binding.numero.text = provider.phone
            Picasso.get().load(provider.image).into(binding.imgProfile);
            view.setOnClickListener {
                val intent = Intent(view.context, DetailProvider::class.java)
                intent.putExtra("id", provider.id);
                intent.putExtra("Nombre", provider.name);
                intent.putExtra("Area", provider.area)
                intent.putExtra("Descripcion", provider.description)
                intent.putExtra("HoraI", provider.startTime);
                intent.putExtra("HoraF", provider.finalTime)
                intent.putExtra("Imagen", provider.image)
                intent.putExtra("telefono", provider.phone)

                view.context.startActivity(intent)
            }

        }
    }
}