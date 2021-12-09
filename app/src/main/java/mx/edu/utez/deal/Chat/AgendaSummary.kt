package mx.edu.utez.deal.Chat

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_agenda_summary.*
import kotlinx.android.synthetic.main.itemevaluation.*
import mx.edu.utez.deal.R


class AgendaSummary : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_summary)

        calificar.setOnClickListener {
            showDialog()
        }

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