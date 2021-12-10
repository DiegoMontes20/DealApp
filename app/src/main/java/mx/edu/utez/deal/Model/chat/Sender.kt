package mx.edu.utez.deal.Model.chat

import com.google.gson.annotations.SerializedName

data class Sender(@SerializedName("id")val id:String,
                  @SerializedName("username")val username:String,
                  @SerializedName("role")val role:String,
                  @SerializedName("notificationToken")val notificationToken:String,
                  @SerializedName("enabled")val enabled:Boolean) {
}