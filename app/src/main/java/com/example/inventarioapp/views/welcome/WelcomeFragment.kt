package com.example.inventarioapp.views.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.text.SimpleDateFormat
import java.util.*
//
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioapp.R
import com.example.inventarioapp.adapters.ProductoAdapter
import com.example.inventarioapp.adapters.TendenciaAdapter
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentWelcomeBinding
import com.example.inventarioapp.entity.Tendencia


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

//
    private lateinit var productoDao: ProductoDao
    private lateinit var productoBajoStockAdapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

//
        val db = InventarioDatabase.getInstance(requireContext())
        productoDao = db.productoDao()

// Mostrar la fecha actual en inglés
        val fechaActual = SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH).format(Date())
        binding.tvFecha.text = "Today is $fechaActual"
//
        loadInventorySummary()
        return binding.root
    }

//datos del carrusel
    private fun getTendenciasFijas(): List<Tendencia> {
        return listOf(
            Tendencia("El futuro de la IA: ¿Cómo será mañana?", "The National", R.drawable.tendencia_ia),
            Tendencia("Intel lanza la nueva generación de chips", "Reuters Tech", R.drawable.tendencia_chips),
            Tendencia("5 claves para mantener la seguridad de tu laptop", "Tech Magazine", R.drawable.tendencia_seguridad),
            Tendencia("Las mejores laptops gaming del 2025", "PC Gamer", R.drawable.tendencia_laptops),
            Tendencia("El nuevo estándar de velocidad en SSDs", "Hardware Insider", R.drawable.tendencia_ssd), // Usa tu nuevo nombre de imagen
            Tendencia("Por qué el 5G cambiará el trabajo remoto", "World Telecom", R.drawable.tendencia_5g), // Usa tu nuevo nombre de imagen
            Tendencia("Los retos de la ciberseguridad en 2026", "Security Today", R.drawable.tendencia_ciber), // Usa tu nuevo nombre de imagen
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tendencias = getTendenciasFijas()
        val tendenciasAdapter = TendenciaAdapter(tendencias)

        binding.rvTendencias.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tendenciasAdapter
        }

// Botón "Lista de Inventario"
        binding.btnInventario.setOnClickListener {
            findNavController().navigate(R.id.page_productos)
        }

// Botón "Charts"
        binding.btnCharts.setOnClickListener {
            Toast.makeText(requireContext(), "Abrir estadísticas", Toast.LENGTH_SHORT).show()
        }

//Botón "Dashboard"
        binding.btnDashboard.setOnClickListener {
            Toast.makeText(requireContext(), "Ir al panel general", Toast.LENGTH_SHORT).show()
        }

// Texto "Ver"
        binding.tvVerTodo.setOnClickListener {
            Toast.makeText(requireContext(), "Mostrando todo el resumen", Toast.LENGTH_SHORT).show()
        }
    }

//

    private fun loadInventorySummary() {
        productoDao.getTotalArticulosCount().observe(viewLifecycleOwner) { total ->
            binding.tvTotalValor.text = total.toString()
        }


        productoDao.getProductosStockBajoCount().observe(viewLifecycleOwner) { count ->
            binding.tvStockBajoValor.text = count.toString()
        }


        productoDao.getProductosSinStockCount().observe(viewLifecycleOwner) { count ->
            binding.tvSinStockValor.text = count.toString()
        }

        productoDao.getSumaTotalStockUnits().observe(viewLifecycleOwner) { totalUnits ->
            val units = totalUnits ?: 0
            binding.tvEntradasValor.text = units.toString()
            binding.tvSalidasValor.text = units.toString()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}