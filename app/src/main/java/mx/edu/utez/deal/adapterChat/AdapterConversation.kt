package mx.edu.utez.deal.adapterChat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.R
import mx.edu.utez.deal.proveedor.chat.ChatProveedor


class AdapterConversation(var activity: Activity) : BaseAdapter() {
    var conversations = ArrayList<Conversation>()
    override fun getCount(): Int {
        return conversations.size
    }

    override fun getItem(position: Int): Any {
        return conversations[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var holder = ConversationViewHolder()
        var myView = convertView
        var conversationInflater = LayoutInflater.from(activity)
        var conversation = conversations[position]

        var messageBody: String

        if (conversation.messages[conversation.messages.lastIndex].sender.role == "Provider")
            messageBody = "Yo: " + conversation.messages[conversation.messages.lastIndex].body
        else
            messageBody = conversation.messages[conversation.messages.lastIndex].body

        myView = conversationInflater.inflate(R.layout.chat_item, null)
        holder.nombreChat = myView.findViewById(R.id.nombreChat)
        holder.mensajeChat = myView.findViewById(R.id.mensajeChat)
        holder.nombreChat!!.text = conversation.client.fullname
        holder.mensajeChat!!.text = messageBody

        holder.nombreChat!!.setOnClickListener {
            val gson = Gson()
            val conversation: String = gson.toJson(conversations[position])
            val intent = Intent(activity, ChatProveedor::class.java)
            intent.putExtra("conversation", conversation)
            activity.startActivity(intent)
        }

        return myView
    }

    internal class ConversationViewHolder {
        var nombreChat: TextView? = null
        var mensajeChat: TextView? = null
    }
}

