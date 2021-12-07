package mx.edu.utez.deal.proveedor.agenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityCitaDetailsBinding

class CitaDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCitaDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}