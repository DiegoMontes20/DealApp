package mx.edu.utez.deal.proveedor.agenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_agenda_list.*
import mx.edu.utez.deal.R

class AgendaList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_list)

        val citaModalFragment = CitaModalFragment()

        listaAgenda.setOnItemClickListener { adapterView, view, position, id ->
            citaModalFragment.show(supportFragmentManager, "CitaModalFragment")


        }

    }

}