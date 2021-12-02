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
import mx.edu.utez.deal.Prefs.PrefsApplication

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        //getFirebaseToken()
        initApp()

    }

    private fun initApp(){
        Handler().postDelayed({
            verifyAuth()
           // startActivity(Intent(this, LoginScreen::class.java))
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
    fun verifyAuth(){
        if(PrefsApplication.prefs.getData("token").isNotEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }else{
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}