package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class Client(@SerializedName("id")val id:String,
                  @SerializedName("name")val name:String,
                  @SerializedName("lastname")val lastname:String,
                  @SerializedName("phone")val phone:String,
                  @SerializedName("fullname")val fullname:String,)