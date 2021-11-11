package mx.edu.utez.deal.Retro

import mx.edu.utez.deal.Model.Client
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("/public/client")
    @Headers("Content-Type: application/json")
    fun createEmployee(@Body requestBody: Client): Call<Void>
    //fun createClient(@Body data: Client):Call<Void>

    @FormUrlEncoded
    @POST("/login")
    suspend fun login(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>

}

