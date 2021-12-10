package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class Appointment(@SerializedName("id")val id:String,
                       @SerializedName("client")val client:Client,
                       @SerializedName("dateTime")val dateTime:String,
                       @SerializedName("approved")val approved:Boolean,
                       @SerializedName("enabled")val enabled:Boolean,
                       @SerializedName("takeout")val takeout:Boolean,
                       @SerializedName("location")val location:LocationAddress?)