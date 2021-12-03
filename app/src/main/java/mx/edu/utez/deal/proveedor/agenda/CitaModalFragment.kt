package mx.edu.utez.deal.proveedor.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_citamodal.*
import mx.edu.utez.deal.R

class CitaModalFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_citamodal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnConfirmarCita.setOnClickListener {
            Toast.makeText(context, "Cita confirmada", Toast.LENGTH_LONG).show()
        }

        btnCancelarCita.setOnClickListener {
            Toast.makeText(context, "Cita cancelada", Toast.LENGTH_LONG).show()
        }
    }

}