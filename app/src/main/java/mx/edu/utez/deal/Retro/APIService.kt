package mx.edu.utez.deal.Retro

import mx.edu.utez.deal.Model.Client
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.Model.UserLogin
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Response

import retrofit2.http.*

interface APIService {
    @POST("/public/client")
    @Headers("Content-Type: application/json")
    fun createEmployee(@Body requestBody: Client): Call<Void>
    //fun createClient(@Body data: Client):Call<Void>

    @POST("/login")
    @Headers("Content-Type: application/json")
    fun login(@Body requestBody: UserLogin): Call<Void>

    @GET("/client/provider")
    @Headers("Content-Type: application/json")
    suspend fun getProvider(): Response<ResponseBody>
}

