package mx.edu.utez.deal.chat2

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import mx.edu.utez.deal.R
import mx.edu.utez.deal.databinding.ActivityAgendaSummaryBinding
import mx.edu.utez.deal.utils.LocationService


class AgendaSummary : AppCompatActivity() {

    companion object {
        var idProveedor = ""
        var latIntent = 0.0
        var longIntent = 0.0
    }

    private lateinit var binding: ActivityAgendaSummaryBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAgendaSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciar()

        val parametros = this.intent.extras

        idProveedor = parametros!!.getString("idProveedor", null)
        latIntent = parametros.getDouble("latitude", 0.0)
        longIntent = parametros.getDouble("longitude", 0.0)

        binding.fechaHora.setText(parametros.getString("fechaHora"))
        binding.ubicacion.setText(parametros.getString("ubicacion"))
        binding.servicio.setText(parametros.getString("descripcion"))
        binding.nombreProveedor.setText(parametros.getString("nombreProveedor"))
        binding.tipoempresa.setText(parametros.getString("tipoServicio"))

        if (parametros.getBoolean("onWay")) {
            binding.abrirMapa.visibility = View.VISIBLE
        } else {
            binding.abrirMapa.visibility = View.GONE
        }

        if (parametros.getBoolean("approved") && parametros.getBoolean("enabled")) {
            binding.calificar.visibility = View.VISIBLE
            binding.btnCancelar.visibility = View.VISIBLE

        } else {
            binding.calificar.visibility = View.GONE
            binding.btnCancelar.visibility = View.GONE
            binding.abrirMapa.visibility = View.GONE
        }

        binding.calificar.setOnClickListener {
            showDialog()
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("idProvider", idProveedor);
            intent.putExtra("nombre", parametros.getString("nombreProveedor"));
            startActivity(intent)
        }

        binding.abrirMapa.setOnClickListener {
            startService(Intent(this, LocationService::class.java))
            val intent = Intent(this, MapAppointmentActivity::class.java)
            intent.putExtra("providerId", idProveedor)
            startActivity(intent)
        }

        binding.btnCancelar.setOnClickListener {

        }
    }

    private fun iniciar() {
        title = "Resumen Agenda"
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