package mx.edu.utez.deal.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mx.edu.utez.deal.AppoinmentProcess.DetailProvider
import mx.edu.utez.deal.Model.Provider
import mx.edu.utez.deal.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        var proveedores:ArrayList<Provider> = ArrayList<Provider>()

        proveedores.add(Provider("Alan Fernandez","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Barbería","https://www.purina-latam.com/sites/g/files/auxxlc391/files/styles/social_share_large/public/Que_debes_saber_antes_de_adoptar_un_gatito.jpg?itok=guFplHEU", "10:21","18:30"))
        proveedores.add(Provider("Diana","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Estetica","https://www.flowww.net/hubfs/Q1_Febrero%202020/BLOG/cual-es-la-mejor-ubicacion-para-abrir-un-centro-de-estetica_flowww.jpg", "10:21","18:30"))
        proveedores.add(Provider("Leo","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Carpinteria","https://previews.123rf.com/images/dolgachov/dolgachov1610/dolgachov161012005/64860816-profesi%C3%B3n-carpinter%C3%ADa-ebanister%C3%ADa-y-el-concepto-de-la-gente-carpintero-con-tablones-de-madera-martil.jpg", "10:21","18:30"))
        proveedores.add(Provider("Alan","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Plomeria","https://www.elmejorplomero.com/imagenes/fontaneros.jpg", "10:21","18:30"))
        proveedores.add(Provider("Alan","7771487584","espero se encuentren bien, soy novato en la programacion android, quiero realizar un slider de imagenes como aparecen en muchas app","Veterinaria","https://www.promedco.com/images/NOTICIAS_2020/reducir-estres-de-mascotas-1.jpg", "10:21","18:30"))

        val appContext = requireContext().applicationContext
        var mi_adaptador = AdapterProvider(proveedores, appContext)

        val list: ListView = binding.lista
        list.adapter = mi_adaptador

        list.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position) as Provider
                val intent = Intent(appContext, DetailProvider::class.java)
                intent.putExtra("Nombre", selectedItemText.name);
                intent.putExtra("Area", selectedItemText.area)
                intent.putExtra("Descripcion", selectedItemText.description)
                intent.putExtra("HoraI", selectedItemText.startTime);
                intent.putExtra("HoraF", selectedItemText.finalTime)
                intent.putExtra("Imagen", selectedItemText.image)
                intent.putExtra("telefono", selectedItemText.phone)

                startActivity(intent);
                println("Selected ${selectedItemText}")
            }

        val root: View = binding.root
/*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}