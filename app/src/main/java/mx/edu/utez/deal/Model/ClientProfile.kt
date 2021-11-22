package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class ClientProfile(@SerializedName("id")val id:String,
                         @SerializedName("name")val name:String,
                         @SerializedName("phone") val phone:String,
                         @SerializedName("lastname") val lastname:String,
                         @SerializedName("user")val User:User,
                         @SerializedName("fullname")val fullname:String)