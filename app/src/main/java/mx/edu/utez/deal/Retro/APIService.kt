package mx.edu.utez.deal.Retro

import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Model.User
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("/public/provider")
    @Headers("Content-Type: application/json")
    fun createProvider(@Body requestBody: Provider): Call<Void>

    @POST("/login")
    @Headers("Content-Type: application/json")
    fun login(@Body requestBody: User): Call<Void>

    @GET("/provider/appointment")
    @Headers("Content-Type: application/json")
    suspend fun getAppointments(): Response<ResponseBody>

    @GET("/provider/profile")
    @Headers("Content-Type: application/json")
    suspend fun getProfile(): Response<ResponseBody>

    @PUT("/provider/profile")
    @Headers("Content-Type: application/json")
    suspend fun updateProfile(@Body requestBody: RequestBody): Response<ResponseBody>

    @PUT("/provider/appointment")
    @Headers("Content-Type: application/json")
    suspend fun updateAppointment(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET("/provider/conversation")
    @Headers("Content-Type: application/json")
    suspend fun getMessages(): Response<ResponseBody>

    @POST("/provider/sendMessage")
    @Headers("Content-Type: application/json")
    suspend fun saveMessage(@Body requestBody: RequestBody):Response<ResponseBody>

}