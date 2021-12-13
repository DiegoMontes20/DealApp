package mx.edu.utez.deal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.util.PermissionChecker

class SplashScreen : AppCompatActivity() {

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        context = this
        initApp()
    }

    private fun initApp() {
        Handler().postDelayed({
            verifyAuth()
        }, 4000)

    }

    fun showToast(message: String, duration: Int = 0) {
        Toast.makeText(this, message, if (duration == 1) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
            .show()
    }

    fun obtenerToken() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                println("Error en firebase ${it.exception}")
                return@OnCompleteListener
            } else {
                val token = it.result
                Log.i("Token", "${token}")
                println("Firebase token ${token}")
            }
        })
    }

    fun verifyAuth() {
        if (prefs.getData("token").isNotEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}