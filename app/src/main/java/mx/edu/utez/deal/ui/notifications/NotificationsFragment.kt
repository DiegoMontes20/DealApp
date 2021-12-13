package mx.edu.utez.deal.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import mx.edu.utez.deal.AboutUs
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.databinding.FragmentNotificationsBinding
import mx.edu.utez.deal.proveedor.perfil.PerfilProveedor

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        root.btnDatosGenerales.setOnClickListener {
            startActivity(Intent(activity,PerfilProveedor::class.java))
        }

        root.btnCerrarSesion.setOnClickListener {
            prefs.deleteAll()
            chageActivity()
        }
        root.sobrenosotros.setOnClickListener {
            val intent = Intent(activity, AboutUs::class.java)
            startActivity(intent)
        }


        return root
    }

    fun chageActivity(){
        val intent = Intent(activity, LoginScreen::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}