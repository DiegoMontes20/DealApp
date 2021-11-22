package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class User (@SerializedName("id")val id:String,
                 @SerializedName("username")val username:String,
                 @SerializedName("role")val role:String,
                 @SerializedName("notificationToken")val token:String,
                 @SerializedName("enabled")val enabled:Boolean=true)