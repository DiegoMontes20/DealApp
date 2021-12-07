package mx.edu.utez.deal.proveedor.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityChatProveedorBinding

class ChatProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityChatProveedorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}