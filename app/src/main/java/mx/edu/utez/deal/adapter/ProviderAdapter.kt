package mx.edu.utez.deal.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ItemproveedorBinding

class ProviderAdapter(val providers:List<ProviderList>):RecyclerView.Adapter<ProviderAdapter.ProviderHolder>() {
    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProviderAdapter.ProviderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProviderHolder(layoutInflater.inflate(R.layout.itemproveedor, parent, false))
    }

    override fun onBindViewHolder(holder: ProviderAdapter.ProviderHolder, position: Int) {
        holder.render(providers[position])

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);

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
            val decodedString: ByteArray = Base64.decode(provider.image, Base64.DEFAULT)
            val decodedByte =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.imgProfile.setImageBitmap(decodedByte)
            view.setOnClickListener {
                PrefsApplication.prefs.save("Imagen", provider.image)
                val intent = Intent(view.context, DetailProvider::class.java)
                intent.putExtra("id", provider.id);
                intent.putExtra("Nombre", provider.name);
                intent.putExtra("Area", provider.area)
                intent.putExtra("Descripcion", provider.description)
                intent.putExtra("HoraI", provider.startTime);
                intent.putExtra("HoraF", provider.finalTime)
                intent.putExtra("telefono", provider.phone)
                intent.putExtra("promedio",provider.evaluationAverage.toString())
                DetailProvider.chat=false
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