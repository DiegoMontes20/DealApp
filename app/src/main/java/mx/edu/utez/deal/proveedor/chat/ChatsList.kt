package mx.edu.utez.deal.proveedor.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.databinding.ActivityChatsListBinding

class ChatsList : AppCompatActivity() {
    private lateinit var  binding: ActivityChatsListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsListBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}