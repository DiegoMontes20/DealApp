package mx.edu.utez.deal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import mx.edu.utez.deal.Login.LoginScreen

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        getFirebaseToken()
        initApp()

    }

    private fun initApp(){
        Handler().postDelayed({
            startActivity(Intent(this, LoginScreen::class.java))
        }, 4000)

    }

    private fun getFirebaseToken(): String? {
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MyFirebaseMsgService->", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.w("MyFirebaseMsgService->", "$token")
        })
        return token
    }
}