package mx.edu.utez.deal.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import mx.edu.utez.deal.InfoPersonal.EditarInformacioActivity
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Prefs.PrefsApplication
import mx.edu.utez.deal.Prefs.PrefsApplication.Companion.prefs
import mx.edu.utez.deal.R
import mx.edu.utez.deal.SobreNosotrsActivity
import mx.edu.utez.deal.databinding.FragmentNotificationsBinding


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

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
/*
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        root.cerrarSesion.setOnClickListener {
            prefs.deleteAll()
            changeActivity()
        }
        root.editarInformacion.setOnClickListener {
            val intent = Intent(activity, EditarInformacioActivity::class.java)
            startActivity(intent)
        }
        root.sobrenosotros.setOnClickListener {
            val intent = Intent(activity, SobreNosotrsActivity::class.java)
            startActivity(intent)
        }


        return root
    }
    fun changeActivity(){
        val intent = Intent(activity, LoginScreen::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}