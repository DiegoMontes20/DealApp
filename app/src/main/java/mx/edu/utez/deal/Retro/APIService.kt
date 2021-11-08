package mx.edu.utez.deal.Retro

import mx.edu.utez.deal.Model.Client
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @POST("/public/client")
    @Headers("Content-Type: application/json")
    suspend fun createEmployee(@Body requestBody: Client): Response<ResponseBody>
    //fun createClient(@Body data: Client):Call<Void>

}

