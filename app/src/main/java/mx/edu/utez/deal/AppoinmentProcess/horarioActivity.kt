package mx.edu.utez.deal.AppoinmentProcess

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_horario.*
import mx.edu.utez.deal.R
import mx.edu.utez.deal.ui.dialog.DatePickerFragment

class horarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario)


        etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        horaselect.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero

            val selectedDate = year.toString() + "/" +   (month + 1) + "/" + day.toString()


            etBirthDate.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }
}