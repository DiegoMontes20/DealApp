package mx.edu.utez.deal.Model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class ProviderList(@SerializedName("id")val id:String,
                   @SerializedName("name")val user:User,
                   @SerializedName("lastname")val name:String,
                   @SerializedName("phone")val location:Objects,
                   @SerializedName("phone")val phone:String,
                   @SerializedName("description")val description:String,
                   @SerializedName("area")val area:String,
                   @SerializedName("image")val image:String,
                   @SerializedName("startTime")val startTime:String,
                   @SerializedName("finalTime")val finalTime:String,
                   @SerializedName("evaluations")val evaluations:ArrayList<Int>,
                   @SerializedName("evaluationAverage")val evaluationAverage:Int,
                   @SerializedName("totalEvaluations")val totalEvaluations:Int
)