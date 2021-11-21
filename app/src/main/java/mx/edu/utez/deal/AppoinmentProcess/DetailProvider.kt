package mx.edu.utez.deal.AppoinmentProcess

import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle_proveedor.*
import mx.edu.utez.deal.R

class DetailProvider : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_proveedor)

        val parametros = this.intent.extras


        nombre.setText( parametros!!.getString("Nombre"))
        giro.setText( parametros!!.getString("Area"))
        info.setText( parametros!!.getString("Descripcion"))
        hora.setText( parametros!!.getString("HoraI") + " - "+ parametros!!.getString("HoraF"))

        telefono.setText( parametros!!.getString("telefono"))



        Picasso.get().load(parametros!!.getString("Imagen")).into(imgProfile);


        solicitar.setOnClickListener {
            startActivity(Intent(this, horarioActivity::class.java))
        }
    }
}