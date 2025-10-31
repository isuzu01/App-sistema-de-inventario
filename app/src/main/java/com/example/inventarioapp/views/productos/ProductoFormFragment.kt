package com.example.inventarioapp.views.productos

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProductoFormBinding
import com.example.inventarioapp.entity.ProductoEntity
import com.example.inventarioapp.repository.FirebaseProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ProductoFormFragment : Fragment() {

    private var _binding: FragmentProductoFormBinding? = null
    private val binding get() = _binding!!

    private  lateinit var productoDao: ProductoDao
    private  lateinit var proveedorDao: ProveedorDao

    private val args: ProductoFormFragmentArgs by navArgs()
    private var productoExistente: ProductoEntity? = null

    private val categorias = listOf("Laptop", "Mouse", "Teclado", "Otros")
    private val action = ProductoFormFragmentDirections.actionBackToProductos()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductoFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = InventarioDatabase.getInstance(requireContext())
        productoDao = db.productoDao()
        proveedorDao = db.proveedorDao()

        setupSpinners()
        setupImageLoader()

        val productoId = args.productoId
        if (productoId > 0) {
            loadProducto(productoId)
            binding.btnSave.text = "Actualizar"
        }
        else { binding.btnSave.text = "Guardar" }

        binding.btnSave.setOnClickListener { saveOrUpdateProducto() }
        binding.btnCancel.setOnClickListener {findNavController().navigate(action)}

    }

    private fun setupImageLoader() {
        binding.btnCargarImagen.setOnClickListener {
            loadImageFromUrl()
        }

        // Cargar imagen automáticamente
        binding.etImagenUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.length > 10) {
                    loadImageFromUrl()
                }
            }
        })
    }


    private fun loadImageFromUrl() {
        val imageUrl = binding.etImagenUrl.text.toString().trim()

        if (imageUrl.isEmpty()) { return }

        binding.progressBarImagen.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
                withContext(Dispatchers.Main) {
                    binding.imgProducto.setImageBitmap(bitmap)
                    binding.progressBarImagen.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBarImagen.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show()
                }
            }
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
                    binding.etImagenUrl.setText(it.imagenUrl)

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
            id = productoExistente?.id ?: 0,
            descripcion = binding.etDescripcion.text.toString().trim(),
            marca = binding.etMarca.text.toString().trim(),
            modelo = binding.etModelo.text.toString().trim(),
            precio = binding.etPrecio.text.toString().toDoubleOrNull()?:0.0,
            stock = binding.etStock.text.toString().toIntOrNull()?: 0,
            nomProveedor = binding.spiProveedor.selectedItem?.toString()?.trim()?: "Sin proveedor",
            nomCategoria = binding.spiCategoria.selectedItem?.toString()?.trim()?: "",
            imagenUrl = binding.etImagenUrl.text.toString().trim()
        )

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                if (productoExistente != null) {
                    FirebaseProductoRepository.actualizarProductoFirebaseYRoom(productoDao, producto)
                } else {
                    FirebaseProductoRepository.insertarProductoFirebaseYRoom(productoDao, producto)
                }

                //FirebaseProductoRepository.insertarProductoFirebaseYRoom(productoDao, producto)

                withContext(Dispatchers.Main) {
                    val msg = if (productoExistente != null) "Producto actualizado" else "Producto agregado"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                    // Notificar actualización
                    parentFragmentManager.setFragmentResult(
                        "producto_actualizar",
                        Bundle().apply { putBoolean("actualizar", true) }
                    )
                    findNavController().navigate(action)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
            binding.etMarca.error = "Marca es obligatoria"
            isValid = false
        }
        if (binding.etModelo.text.isNullOrEmpty()) {
            binding.etModelo.error = "Modelo es obligatorio"
            isValid = false
        }
        if (binding.etPrecio.text.isNullOrEmpty()) {
            binding.etPrecio.error = "Precio es obligatorio"
            isValid = false
        } else {
            val precio = binding.etPrecio.text.toString().toDoubleOrNull()
            if (precio == null || precio <= 0) {
                binding.etPrecio.error = "Precio debe ser mayor a 0"
                isValid = false
            }
        }
        if (binding.etStock.text.isNullOrEmpty()) {
            binding.etStock.error = "Stock es obligatorio"
            isValid = false
        } else {
            val stock = binding.etStock.text.toString().toIntOrNull()
            if (stock == null || stock < 0) {
                binding.etStock.error = "Stock debe ser un número válido"
                isValid = false
            }
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}