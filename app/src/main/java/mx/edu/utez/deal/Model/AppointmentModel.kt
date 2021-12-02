package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName

data class AppointmentModel(@SerializedName("id")val id:String,
                            @SerializedName("provider")val provider:ProviderList,
                            @SerializedName("dateTime")val dateTime:String,
                            @SerializedName("approved")val approved:Boolean,
                            @SerializedName("enabled")val enabled:Boolean,
                            @SerializedName("takeout")val takeout:Boolean)