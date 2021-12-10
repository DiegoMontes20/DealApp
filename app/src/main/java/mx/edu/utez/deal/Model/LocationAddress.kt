package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class LocationAddress(@SerializedName("name")val name:String,
                           @SerializedName("latitude")val latitude:String,
                           @SerializedName("longitude")val longitude:String)