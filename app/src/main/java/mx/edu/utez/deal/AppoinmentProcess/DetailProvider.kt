package mx.edu.utez.deal.AppoinmentProcess

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle_proveedor.*
import mx.edu.utez.deal.Prefs.PrefsApplication
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

        val image = PrefsApplication.prefs.getData("Imagen")

        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        val decodedByte =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.imgProfile.setImageBitmap(decodedByte)



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

    override fun onBackPressed() {
        super.onBackPressed()
        PrefsApplication.prefs.save("Imagen", "")
    }
}