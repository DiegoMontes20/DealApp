package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class Provider(@SerializedName("name")val name:String,
                    @SerializedName("phone")val phone:String,
                    @SerializedName("description")val description:String,
                    @SerializedName("area")val area:String,
                    @SerializedName("image")val image:String,
                    @SerializedName("startTime")val startTime:String,
                    @SerializedName("finalTime")val finalTime:String,
                    @SerializedName("user")val User:User,
                    @SerializedName("location")var location: LocationAddress?,
                    @SerializedName("evaluationAverage")var evaluationAverage: Int?

)