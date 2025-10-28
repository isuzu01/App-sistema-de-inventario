package com.example.inventarioapp.views.proveedores

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.inventarioapp.R
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProveedorAddBinding
import com.example.inventarioapp.entity.ProveedorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProveedorAddFragment : Fragment() {

    private var _binding: FragmentProveedorAddBinding? = null
    private val binding get() = _binding!!


    private val proveedorDao: ProveedorDao by lazy {
        InventarioDatabase.getInstance(requireContext()).proveedorDao()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProveedorAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar botones
        binding.btnSave.setOnClickListener { saveProveedor() }
        binding.btnCancel.setOnClickListener {

            // Cierra el fragmento (regresa al anterior)
            findNavController().navigate(R.id.page_proveedores)
        }
    }
    private fun saveProveedor() {
        if (!validateFields()) {
            return
        }
        val nombreEmpresa = binding.etNombreEmpresa.text.toString().trim()
        val ruc = binding.etRuc.text.toString().trim()
        val esNacional = binding.cbEsNacional.isChecked
        val direccion = binding.etDireccion.text.toString().trim()
        val nombreContacto = binding.etNombreContacto.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()

        val nuevoProveedor = ProveedorEntity(
            nombreEmpresa = nombreEmpresa,
            ruc = ruc,
            esNacional = esNacional,
            direccion = direccion,
            nombreContacto = nombreContacto,
            correo = correo,
            telefono = telefono
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val id = proveedorDao.insertProveedor(nuevoProveedor)

            withContext(Dispatchers.Main){
                if (id > 0) {
                    Toast.makeText(requireContext(), "Proveedor agregado correctamente!", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.setFragmentResult(
                        "proveedor_actualizar",
                        Bundle().apply { putBoolean("actualizar", true) })


                } else {
                    Toast.makeText(requireContext(), "Error al guardar el proveedor.", Toast.LENGTH_SHORT).show()
                }
                findNavController().navigate(R.id.page_proveedores)
            }
        }
    }
    private fun validateFields(): Boolean {
        var isValid = true

        binding.etNombreEmpresa.error = null
        binding.etNombreContacto.error = null
        binding.etCorreo.error = null

        if (binding.etNombreEmpresa.text.isNullOrEmpty()) {
            binding.etNombreEmpresa.error = "Nombre de empresa es obligatorio"
            isValid = false
        }
        if (binding.etNombreContacto.text.isNullOrEmpty()) {
            binding.etNombreContacto.error = "Nombre de contacto es obligatorio"
            isValid = false
        }
        if (binding.etCorreo.text.isNullOrEmpty()) {
            binding.etCorreo.error = "Correo es obligatorio"
            isValid = false
        }

        return isValid
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}