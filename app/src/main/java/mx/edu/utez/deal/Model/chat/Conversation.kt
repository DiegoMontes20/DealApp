package mx.edu.utez.deal.Model.chat

import com.google.gson.annotations.SerializedName
import mx.edu.utez.deal.Model.Client

data class Conversation(@SerializedName("id")val id:String,
                        @SerializedName("client")val client: Client,
                        @SerializedName("messages")val messages: ArrayList<Message>,
) {
}