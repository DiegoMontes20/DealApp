package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

class UserLogin (
                 @SerializedName("username")val username:String,
                 @SerializedName("password")val password:String,
                )