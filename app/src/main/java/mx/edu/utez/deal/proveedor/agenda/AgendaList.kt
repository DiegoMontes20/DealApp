package mx.edu.utez.deal.proveedor.agenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.edu.utez.deal.databinding.ActivityAgendaListBinding

class AgendaList : AppCompatActivity() {
    private lateinit var binding: ActivityAgendaListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val citaModalFragment = CitaModalFragment()

        binding.listaAgenda.setOnItemClickListener { adapterView, view, position, id ->
            citaModalFragment.show(supportFragmentManager, "CitaModalFragment")


        }

    }

}