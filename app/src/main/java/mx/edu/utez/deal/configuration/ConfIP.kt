package mx.edu.utez.deal.configuration


import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

import java.io.IOException

class ConfIP {
    companion object{
        const val IP = "http://192.168.0.9:8080"

        val getHeader =fun(): Interceptor {
            return Interceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .header("Authorization", prefs.getData("token"))
                        .build()
                chain.proceed(request)
            }
        }

    }


}