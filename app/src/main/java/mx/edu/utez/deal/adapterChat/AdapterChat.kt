package mx.edu.utez.deal.adapterChat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import mx.edu.utez.deal.R


class AdapterChat(context2: Context) : BaseAdapter() {

    var mensajes = ArrayList<ModelChat>()
    var context = context2

    override fun getCount(): Int {
        return mensajes.size
    }

    override fun getItem(position: Int): Any {
        return mensajes.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun add(mensaje: ModelChat){
        mensajes.add(mensaje)
        this.notifyDataSetChanged()
    }

    fun clear(){
        mensajes.clear()
        this.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder = MessageViewHolder()
        var myView = convertView
        var messageInflater = LayoutInflater.from(context)
        var mensaje = mensajes.get(position).mensaje

        if(mensajes.get(position).usuario.equals("Provider")){
            myView= messageInflater.inflate(R.layout.mi_mensaje, null)
            holder.cuerpoDelMensaje = myView.findViewById(R.id.cuerpoDelMensaje)
            holder.cuerpoDelMensaje!!.setText(mensaje)
        }else{
            myView= messageInflater.inflate(R.layout.su_mensaje, null)
            holder.cuerpoDelMensaje = myView.findViewById(R.id.cuerpoDelMensaje)
            holder.cuerpoDelMensaje!!.setText(mensaje)
        }

        return myView
    }

    internal class MessageViewHolder{
        var cuerpoDelMensaje: TextView? = null
    }
}

