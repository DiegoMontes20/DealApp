package mx.edu.utez.deal.Retro

import mx.edu.utez.deal.Model.Client
import mx.edu.utez.deal.Model.ProviderList
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.Model.UserLogin
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

    @POST("/login")
    @Headers("Content-Type: application/json")
    fun login(@Body requestBody: UserLogin): Call<Void>

    @GET("/client/provider")
    @Headers("Content-Type: application/json")
    suspend fun getProviders(): Response<ResponseBody>

    @GET("/client/provider/location")
    @Headers("Content-Type: application/json")
    suspend fun getProviderLocation(@Query("providerId") providerId:String?): Response<ResponseBody>

    @GET("/client/profile")
    @Headers("Content-Type: application/json")
    suspend fun getProfile(): Response<ResponseBody>

    @PUT("/client/profile")
    @Headers("Content-Type: application/json")
    suspend fun updateProfile(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/client/appointment")
    @Headers("Content-Type: application/json")
    suspend fun saveAppointment(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET("/client/appointment")
    @Headers("Content-Type: application/json")
    suspend fun getAppointments(): Response<ResponseBody>

    @GET("/client/appointment/available")
    @Headers("Content-Type: application/json")
    suspend fun getAppointmentsHoras(@Query("date") date:String,
                                     @Query("providerId") providerId:String):Response<ResponseBody>

    @GET("/client/conversation")
    @Headers("Content-Type: application/json")
    suspend fun getMessages(): Response<ResponseBody>

    @POST("/client/sendMessage")
    @Headers("Content-Type: application/json")
    suspend fun saveMessage(@Body requestBody: RequestBody):Response<ResponseBody>

    @HTTP(method = "DELETE", path = "/client/appointment", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun deleteAppoinment(@Body requestBody: RequestBody):Response<ResponseBody>

    @PUT("/client/appointment")
    @Headers("Content-Type: application/json")
    suspend fun updateAppoinment(@Body requestBody: RequestBody):Response<ResponseBody>

    @GET("/client/provider")
    @Headers("Content-Type: application/json")
    suspend fun getProvidersByName(@Query("name") name:String):Response<ResponseBody>

}

