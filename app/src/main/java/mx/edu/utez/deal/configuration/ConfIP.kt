package mx.edu.utez.deal.configuration


import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

import java.io.IOException

class ConfIP {
    companion object{
        const val IP = "http://209.50.56.129:8080"
    }
}
// 192.168.0.11
//http://209.50.56.129:8080