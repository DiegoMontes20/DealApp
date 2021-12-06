package mx.edu.utez.deal.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.R

class ChatAdapter(context2: Context) : BaseAdapter()  {

    var mensajes = ArrayList<Conversation>()
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder = MessageViewHolder()
        var myView = convertView
        var messageInflater = LayoutInflater.from(context)
        var mensaje = mensajes.get(position).messages.get(position).body
        println("Entro a getView")

        if(mensajes.get(position).messages.get(position).sender.role.equals("Client")){
           // myView= messageInflater.inflate(R.layout)
        }

        return myView

    }
}

internal class MessageViewHolder{
    var cuerpoDelMensaje: TextView? = null
}