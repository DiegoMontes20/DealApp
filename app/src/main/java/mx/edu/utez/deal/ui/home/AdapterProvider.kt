package mx.edu.utez.deal.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.R

class AdapterProvider(items: ArrayList<Provider>, ctx: Context) :
    ArrayAdapter<Provider>(ctx, R.layout.itemproveedor, items) {

    val contexto: Context = ctx

    //view holder is used to prevent findViewById calls
    private class AdapterListHolder {
        var tipoServicio: TextView? = null
        var info: TextView? = null
        var horario: TextView? = null
        var numero: TextView? = null
        var imgProfile: ImageView? = null
//componentes de la vista

    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view

        val viewHolder: AdapterListHolder

        val element = getItem(i)

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.itemproveedor, viewGroup, false)

            viewHolder = AdapterListHolder()
            viewHolder.tipoServicio = view!!.findViewById<View>(R.id.tipoServicio) as TextView
            viewHolder.info = view!!.findViewById<View>(R.id.info) as TextView
            viewHolder.horario = view!!.findViewById<View>(R.id.horario) as TextView
            viewHolder.numero = view!!.findViewById<View>(R.id.numero) as TextView
            viewHolder.imgProfile = view!!.findViewById<View>(R.id.imgProfile) as ImageView

        } else {
            //no need to call findViewById, can use existing ones from saved view holder
            viewHolder = view.tag as AdapterListHolder
        }

        //asigancion de valor

        viewHolder.tipoServicio!!.text = element!!.name + "\n"+ element!!.area
        viewHolder.info!!.text = element!!.description
        viewHolder.horario!!.text = element!!.startTime +" - "+ element!!.finalTime
        viewHolder.numero!!.text = element!!.phone

        Picasso.get().load(element.image).into(viewHolder.imgProfile);

        //setBackgroundColor(context.resources.getColor(R.color.red))


        view.tag = viewHolder

        return view
    }

}