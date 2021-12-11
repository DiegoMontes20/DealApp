package mx.edu.utez.deal.chat2

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import mx.edu.utez.deal.AppoinmentProcess.MapsActivity
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityAgendaSummaryBinding
import mx.edu.utez.deal.utils.LocationService


class AgendaSummary : AppCompatActivity() {

    companion object{
        var idProveedor = ""
        var latIntent=""
        var longIntent=""
    }

    private lateinit var binding: ActivityAgendaSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityAgendaSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciar()

        val parametros = this.intent.extras

        idProveedor =  parametros!!.getString("idProveedor").toString()
        latIntent =  parametros!!.getString("latitude").toString()
        longIntent =  parametros!!.getString("longitude").toString()

        binding.fechaHora.setText(parametros!!.getString("fechaHora"))
        binding.ubicacion.setText(parametros!!.getString("ubicacion"))
        binding.servicio.setText(parametros!!.getString("descripcion"))
        binding.nombreProveedor.setText(parametros!!.getString("nombreProveedor"))
        binding.tipoempresa.setText(parametros!!.getString("tipoServicio"))

        binding.calificar.setOnClickListener {
            showDialog()
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("idProvider", idProveedor);
            intent.putExtra("nombre",  parametros!!.getString("nombreProveedor"));
            startActivity(intent)
        }

        binding.abrirMapa.setOnClickListener {
            startService(Intent(this, LocationService::class.java))
            startActivity(Intent(this, MapAppointmentActivity::class.java))
        }

    }

    private fun iniciar(){
        setTitle("Resumen Agenda")
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.itemevaluation)
        val body = dialog.findViewById(R.id.numero) as EditText

        val yesBtn = dialog.findViewById(R.id.guardar) as Button
        val noBtn = dialog.findViewById(R.id.cerrar) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }



}