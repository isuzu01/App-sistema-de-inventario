package com.example.inventarioapp.views.proveedores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventarioapp.R
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProveedorFormBinding
import com.example.inventarioapp.entity.ProveedorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProveedorFormFragment : Fragment() {

    private var _binding: FragmentProveedorFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var proveedorDao: ProveedorDao

    private val args: ProveedorFormFragmentArgs by navArgs()

    private var proveedorExistente: ProveedorEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProveedorFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = InventarioDatabase.getInstance(requireContext().applicationContext)
        proveedorDao = db.proveedorDao()

        val proveedorId = args.proveedorId
        if (proveedorId > 0) {
            loadProveedor(proveedorId)
            binding.btnSave.text = "Actualizar"
        } else {
            binding.btnSave.text = "Guardar"
        }

        // Configurar botones
        binding.btnSave.setOnClickListener { saveOrUpdateProveedor() }
        binding.btnCancel.setOnClickListener {  findNavController().navigate(R.id.page_proveedores) }
    }

    private fun loadProveedor(id:Long){
        lifecycleScope.launch(Dispatchers.IO) {
            val proveedor = proveedorDao.getProveedorById(id)
            proveedorExistente = proveedor

            withContext(Dispatchers.Main) {
                proveedor?.let {
                    binding.etNombreEmpresa.setText(it.nombreEmpresa)
                    binding.etRuc.setText(it.ruc)
                    binding.cbEsNacional.isChecked = it.esNacional
                    binding.etDireccion.setText(it.direccion)
                    binding.etNombreContacto.setText(it.nombreContacto)
                    binding.etCorreo.setText(it.correo)
                    binding.etTelefono.setText(it.telefono)
                }
            }
        }
    }
    private fun saveOrUpdateProveedor() {
        if (!validateFields()) {
            return
        }
        val proveedor = ProveedorEntity(
            id = proveedorExistente?.id ?: 0,
            nombreEmpresa = binding.etNombreEmpresa.text.toString().trim(),
            ruc = binding.etRuc.text.toString().trim(),
            esNacional = binding.cbEsNacional.isChecked,
            direccion = binding.etDireccion.text.toString().trim(),
            nombreContacto = binding.etNombreContacto.text.toString().trim(),
            correo = binding.etCorreo.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim()
        )

        lifecycleScope.launch(Dispatchers.IO) {

            if (proveedorExistente != null) {
                proveedorDao.updateProveedor(proveedor)
            } else {
                proveedorDao.insertProveedor(proveedor)
            }
            withContext(Dispatchers.Main){
                val mensaje = if (proveedorExistente != null)
                                "Proveedor actualizado correctamente!"
                            else
                                "Proveedor agregado correctamente!"

                Toast.makeText(requireContext(),  mensaje, Toast.LENGTH_SHORT).show()

                parentFragmentManager.setFragmentResult(
                    "proveedor_actualizar",
                    Bundle().apply { putBoolean("actualizar", true) })

                /*
            } else {
                Toast.makeText(requireContext(), "Error al guardar el proveedor.", Toast.LENGTH_SHORT).show()
            } */
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