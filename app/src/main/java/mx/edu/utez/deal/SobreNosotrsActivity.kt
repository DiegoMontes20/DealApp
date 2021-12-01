package mx.edu.utez.deal

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_sobre_nosotrs.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import mx.edu.utez.deal.ui.notifications.NotificationsFragment

class SobreNosotrsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_nosotrs)

        regresar.setOnClickListener {
            onBackPressed()

        }

    }
}