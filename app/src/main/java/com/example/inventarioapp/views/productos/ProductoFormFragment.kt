package com.example.inventarioapp.views.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProductoBinding
import com.example.inventarioapp.databinding.FragmentProductoFormBinding
import com.example.inventarioapp.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductoFormFragment : Fragment() {

    private var _binding: FragmentProductoFormBinding? = null
    private val binding get() = _binding!!

    private  lateinit var productoDao: ProductoDao
    private  lateinit var proveedorDao: ProveedorDao

    private val args: ProductoFormFragmentArgs by navArgs()
    private var productoExistente: ProductoEntity? = null

    val categorias = listOf("Laptop", "Mouse", "Teclado", "Otros")
    val action = ProductoFormFragmentDirections.actionBackToProductos()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = InventarioDatabase.getInstance(requireContext().applicationContext)
        productoDao = db.productoDao()
        proveedorDao = db.proveedorDao()

        setupSpinners()

        val productoId = args.productoId
        if (productoId > 0) {
            loadProducto(productoId)
            binding.btnSave.text = "Actualizar"
        }
        else { binding.btnSave.text = "Guardar" }

        binding.btnSave.setOnClickListener { saveOrUpdateProducto() }
        binding.btnCancel.setOnClickListener {

            findNavController().navigate(action)
        }

    }


    private fun loadProducto(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val producto = productoDao.getProductoById(id)
            productoExistente = producto

            val proveedores = proveedorDao.getAllProveedores()
            val nombresProveedores = proveedores.map { it.nombreEmpresa }

            withContext(Dispatchers.Main) {
                producto?.let {
                    binding.etDescripcion.setText(it.descripcion)
                    binding.etMarca.setText(it.marca)
                    binding.etModelo.setText(it.modelo)
                    binding.etPrecio.setText(it.precio.toString())
                    binding.etStock.setText(it.stock.toString())

                    val indexCategoria = categorias.indexOf(it.nomCategoria)
                    if (indexCategoria != -1) binding.spiCategoria.setSelection(indexCategoria)

                    val indexProveedor = nombresProveedores.indexOf(it.nomProveedor)
                    if (indexProveedor != -1) binding.spiProveedor.setSelection(indexProveedor)

                }
            }
        }
    }

    private fun setupSpinners() {

        val categoriaAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spiCategoria.adapter = categoriaAdapter

        // Spinner de proveedores (desde BD)
        lifecycleScope.launch(Dispatchers.IO) {
            val proveedores = proveedorDao.getAllProveedores()
            val nombresProveedores = proveedores.map { it.nombreEmpresa }

            withContext(Dispatchers.Main) {
                val proveedorAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombresProveedores
                )
                proveedorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spiProveedor.adapter = proveedorAdapter
            }
        }
    }
    private fun saveOrUpdateProducto() {
        if(!validateFields()) {return}

        val producto = ProductoEntity(
            id = productoExistente?.id?: 0,
            descripcion = binding.etDescripcion.text.toString().trim(),
            marca = binding.etMarca.text.toString().trim(),
            modelo = binding.etModelo.text.toString().trim(),
            precio = binding.etPrecio.text.toString().toDoubleOrNull()?:0.0,
            stock = binding.etStock.text.toString().toIntOrNull()?: 0,
            nomProveedor = binding.spiProveedor.selectedItem?.toString()?.trim()?: "",
            nomCategoria = binding.spiCategoria.selectedItem?.toString()?.trim()?: ""
        )


        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (productoExistente != null) {
                    productoDao.updateProducto(producto)
                } else {
                    productoDao.insertProducto(producto)
                }
                withContext(Dispatchers.Main) {
                    val mensaje = if (productoExistente != null)
                        "Producto actualizado correctamente!"
                    else
                        "Producto agregado correctamente!"

                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.setFragmentResult(
                        "producto_actualizar",
                        Bundle().apply { putBoolean("actualizar", true) })

                    findNavController().navigate(action)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        binding.etDescripcion.error = null
        binding.etMarca.error = null
        binding.etModelo.error = null
        binding.etPrecio.error = null
        binding.etStock.error = null

        if (binding.etDescripcion.text.isNullOrEmpty()) {
            binding.etDescripcion.error = "Descripcion del Producto es obligatorio"
            isValid = false
        }
        if (binding.etMarca.text.isNullOrEmpty()) {
            binding.etMarca.error = "Nombre de contacto es obligatorio"
            isValid = false
        }
        if (binding.etModelo.text.isNullOrEmpty()) {
            binding.etModelo.error = "Correo es obligatorio"
            isValid = false
        }
        if (binding.etPrecio.text.isNullOrEmpty()) {
            binding.etPrecio.error = "Correo es obligatorio"
            isValid = false
        }
        if (binding.etStock.text.isNullOrEmpty()) {
            binding.etStock.error = "Correo es obligatorio"
            isValid = false
        }


        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}