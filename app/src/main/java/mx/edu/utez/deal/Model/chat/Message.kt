package mx.edu.utez.deal.Model.chat

import com.google.gson.annotations.SerializedName

data class Message(@SerializedName("body")val body:String,
                   @SerializedName("dateTime")val dateTime:String,
                   @SerializedName("sender")val sender:Sender) {
}