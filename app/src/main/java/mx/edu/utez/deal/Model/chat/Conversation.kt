package mx.edu.utez.deal.Model.chat

import com.google.gson.annotations.SerializedName
import mx.edu.utez.deal.Model.ProviderList

data class Conversation(@SerializedName("id")val id:String,
                        @SerializedName("provider")val provider: ProviderList,
                        @SerializedName("messages")val messages: ArrayList<Message>,
) {
}