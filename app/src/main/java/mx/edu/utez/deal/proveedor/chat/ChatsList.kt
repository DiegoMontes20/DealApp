package mx.edu.utez.deal.proveedor.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import mx.edu.utez.deal.Model.Appointment
import mx.edu.utez.deal.databinding.ActivityChatsListBinding
import mx.edu.utez.deal.proveedor.agenda.CitaDetails

class ChatsList : AppCompatActivity() {
    private lateinit var  binding: ActivityChatsListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listaChats.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position) as Appointment
                val intent = Intent(applicationContext, ChatProveedor::class.java)
                intent.putExtra("nombreCliente", selectedItemText.client.fullname);
                intent.putExtra("numeroCliente", selectedItemText.client.phone);
                intent.putExtra("dateTime", selectedItemText.dateTime)
                startActivity(intent);
            }


    }
}