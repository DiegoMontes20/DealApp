package mx.edu.utez.deal.adapterChat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import mx.edu.utez.deal.Model.chat.Conversation
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ChatItemBinding
import mx.edu.utez.deal.proveedor.chat.ChatProveedor


class AdapterConversation(var activity: Activity) :
    RecyclerView.Adapter<AdapterConversation.ProviderHolder>() {
    var conversations = ArrayList<Conversation>()

    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    class ProviderHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ChatItemBinding.bind(view)
        fun render(conversation: Conversation) {
            val messageBody: String
            if (conversation.messages[conversation.messages.lastIndex].sender.role == "Provider")
                messageBody = "Yo: " + conversation.messages[conversation.messages.lastIndex].body
            else
                messageBody = conversation.messages[conversation.messages.lastIndex].body
            var name = conversation.client.name + " " + conversation.client.lastname[0] + "."
            binding.nombreChat.text = name
            binding.mensajeChat.text = messageBody
            binding.nombreChat.setOnClickListener {
                val gson = Gson()
                val intent = Intent(view.context, ChatProveedor::class.java)
                intent.putExtra("conversation", gson.toJson(conversation))
                view.context.startActivity(intent)
            }
        }
        fun clearAnimation() {
            view.clearAnimation()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProviderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProviderHolder(
            layoutInflater.inflate(
                R.layout.chat_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProviderHolder, position: Int) {
        holder.render(conversations[position])

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
    }

    override fun getItemCount(): Int {
        return conversations.size
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

