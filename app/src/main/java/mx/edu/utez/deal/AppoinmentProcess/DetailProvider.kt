package mx.edu.utez.deal.AppoinmentProcess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle_proveedor.*
import mx.edu.utez.deal.chat2.AgendaSummary

import mx.edu.utez.deal.databinding.ActivityDetalleProveedorBinding

class DetailProvider : AppCompatActivity() {
    private lateinit var binding : ActivityDetalleProveedorBinding

    companion object{
        var id = ""
        var nombrePro=""
        var descripcion=""
        var tipo=""
        var tiempo=""
        var chat=false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val parametros = this.intent.extras
        binding.nombre.setText( parametros!!.getString("Nombre"))
        binding.giro.setText( parametros!!.getString("Area"))
        binding.info.setText( parametros!!.getString("Descripcion"))
        binding.hora.setText( parametros!!.getString("HoraI") + " - "+ parametros!!.getString("HoraF"))
        binding.telefono.setText( parametros!!.getString("telefono")) //promedio
        binding.promedio.setText( parametros!!.getString("promedio"))

        id= parametros!!.getString("id").toString()
        nombrePro= parametros!!.getString("Nombre").toString()
        descripcion= parametros!!.getString("Descripcion").toString()
        tipo= parametros!!.getString("Area").toString()
        Picasso.get().load(parametros!!.getString("Imagen")).into(imgProfile);


        //mostrar/esconder botón chat
        binding.viewChat.isVisible = chat

        //esconder/mostrar botón de solicitar
        binding.solicitar.isVisible =!chat


        binding.solicitar.setOnClickListener {
            startActivity(Intent(this, horarioActivity::class.java))
        }

        binding.getChat.setOnClickListener {
            val intent = Intent(this, AgendaSummary::class.java)
            intent.putExtra("idProvider", parametros!!.getString("id").toString());
            intent.putExtra("nombre",  parametros!!.getString("Nombre"));
            startActivity(intent)
        }
    }
}