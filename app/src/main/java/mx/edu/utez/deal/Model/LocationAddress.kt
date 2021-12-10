package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class LocationAddress(@SerializedName("name")val name:String,
                           @SerializedName("latitude")val latitude:Double,
                           @SerializedName("longitude")val longitude:Double)