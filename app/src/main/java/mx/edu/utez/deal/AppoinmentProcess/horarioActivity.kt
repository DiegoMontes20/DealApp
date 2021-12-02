package mx.edu.utez.deal.AppoinmentProcess

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_horario.*
import mx.edu.utez.deal.databinding.ActivityHorarioBinding
import mx.edu.utez.deal.ui.dialog.DatePickerFragment
import mx.edu.utez.deal.utils.LocationService

class horarioActivity : AppCompatActivity() {

    companion object{
        var fecha=""
    }



    private lateinit var binding:ActivityHorarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHorarioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.horaselect.setOnClickListener {
            if(binding.etBirthDate.text.isEmpty()){
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_LONG).show()
            }else{
                startService(Intent(this, LocationService::class.java))
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }

        binding.regresar.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero

            val selectedDate = year.toString() + "-" +   (month + 1) + "-" + day.toString()

            fecha=selectedDate
            println("FECHA DEL CALENDAR -> ${fecha}")


            etBirthDate.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }
}