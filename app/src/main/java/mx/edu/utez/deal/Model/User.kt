package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class User (@SerializedName("username")val username:String,
                 @SerializedName("password")val password:String,
                 @SerializedName("notificationToken")val token:String)