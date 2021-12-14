package mx.edu.utez.deal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.databinding.ActivityAboutUsBinding

class AboutUs : AppCompatActivity() {
    private lateinit var binding:ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this).supportActionBar!!.hide()
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.regresar.setOnClickListener {
            onBackPressed()
        }
    }
}