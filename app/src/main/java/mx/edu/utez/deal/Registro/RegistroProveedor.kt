package mx.edu.utez.deal.Registro

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import mx.edu.utez.deal.Configuration.ConfIP
import mx.edu.utez.deal.Login.LoginScreen
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.Model.User
import mx.edu.utez.deal.Retro.APIService
import mx.edu.utez.deal.databinding.ActivityRegistroProveedorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException
import java.io.InputStream

import android.graphics.BitmapFactory
import android.annotation.SuppressLint
import android.util.Base64
import mx.edu.utez.deal.R
import java.io.ByteArrayOutputStream


class RegistroProveedor : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroProveedorBinding
    private val SELECT_ACTIVITY = 50
    private var imageUri: Uri? = null
    private lateinit var decodeImage: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultImage()


        binding.inputImagen.setOnClickListener {
            ImageController.selectPhoto(this, SELECT_ACTIVITY);
        }

        binding.backLogin.setOnClickListener {
            changeLogin()
        }

        binding.btnProRegister.setOnClickListener {
            if (check()) {
                Snackbar.make(it, "Por favor llena los campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Handler().postDelayed({

                    create(
                        binding.reUsername.text.toString(),
                        binding.rePassword.text.toString(),
                        binding.reName.text.toString(),
                        binding.rePhone.text.toString(),
                        binding.reDescripcion.text.toString(),
                        binding.reArea.text.toString(),
                        binding.reStartTime.text.toString(),
                        binding.reFinalTime.text.toString(),
                    )

                }, 2000)
            }
        }
    }

    fun changeLogin() {
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun check(): Boolean {
        return binding.reUsername.text.isEmpty() ||
                binding.rePassword.text.isEmpty() ||
                binding.reName.text.isEmpty() ||
                binding.rePhone.text.isEmpty() ||
                binding.reDescripcion.text.isEmpty() ||
                binding.reArea.text.isEmpty() ||
                binding.reStartTime.text.isEmpty() ||
                binding.reFinalTime.text.isEmpty()
    }

    fun create(
        username: String, password: String, name: String,
        phone: String, description: String, area: String,
        startTime: String, finalTime: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfIP.IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                println("Error en firebase ${it.exception}")
                return@OnCompleteListener
            } else {
                val token = it.result
                //Log.i("SI hay", token.toString())
                val user = User(username, password, token.toString())
                val provider =
                    Provider(
                        name,
                        phone,
                        description,
                        area,
                        decodeImage,
                        startTime,
                        finalTime,
                        user,
                        null
                    )
               // println("Cliente ${provider.toString()}")
                service.createProvider(provider).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Usuarios registrado", Toast.LENGTH_LONG
                            ).show()
                            changeActivity()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error al registrar", Toast.LENGTH_LONG)
                            .show()
                    }
                })


            }
        })

    }

    fun changeActivity() {
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setDefaultImage() {
        val imageDefault: Bitmap =
            BitmapFactory.decodeResource(this.resources, R.drawable.image_default)
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageDefault.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        decodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
        binding.inputImagen.setImageBitmap(
            Bitmap.createScaledBitmap(
                imageDefault,
                140,
                140,
                false
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == SELECT_ACTIVITY && resultCode == Activity.RESULT_OK -> {
                imageUri = data!!.data

                val selectedPath = imageUri!!.path
                if (selectedPath != null) {
                    var imageStream: InputStream? = null
                    try {
                        imageStream = contentResolver.openInputStream(imageUri!!)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    val bmp = BitmapFactory.decodeStream(imageStream)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    decodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    binding.inputImagen.setImageBitmap(
                        Bitmap.createScaledBitmap(
                            bmp,
                            binding.inputImagen.width,
                            binding.inputImagen.height,
                            false
                        )
                    )
                }
            }
        }
    }
}