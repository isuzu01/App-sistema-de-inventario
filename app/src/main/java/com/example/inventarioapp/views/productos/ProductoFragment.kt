package com.example.inventarioapp.views.productos

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioapp.adapters.ProductoAdapter
import com.example.inventarioapp.R
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProductoBinding
import com.example.inventarioapp.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductoFragment : Fragment(R.layout.fragment_producto) {

    private  var _binding: FragmentProductoBinding? = null
    private  val binding get() = _binding!!

    private lateinit var mAdapter: ProductoAdapter
    private lateinit var productoDao: ProductoDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = InventarioDatabase.getInstance(requireContext().applicationContext)
        productoDao = db.productoDao()

        setupRecyclerView()
        setupSpinner()
        setupSearchListener()
        setupListeners()

        parentFragmentManager.setFragmentResultListener(
            "producto_actualizar",
            viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean("actualizar", false)) {
                val currentPosition = binding.spinnerSort.selectedItemPosition
                loadAllProductos(currentPosition)
                Toast.makeText(requireContext(), "Lista de producto actualizada.", Toast.LENGTH_SHORT).show()
            }
        }

        loadAllProductos(0)
    }

    private fun setupSpinner(){
        val adapterSpinner = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options_Producto,
            android.R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = adapterSpinner
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                loadAllProductos(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = ProductoAdapter(
            onClick = { productoId: Long -> onProductoClick(productoId) },
            onLongClick = { producto: ProductoEntity ->showDeleteConfirmationDialog(producto)
                true
            }
        )
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupSearchListener() {
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchProductos(query)
            }

        })
    }

    private fun setupListeners() {
        binding.ibtnAdd.setOnClickListener {
            findNavController().navigate(
                R.id.action_to_form_producto,
                Bundle().apply{ putLong("productoId", 0L) })
        }
    }
    private fun onProductoClick(productoId: Long) {

        val bundle = Bundle().apply {
            putLong("productoId", productoId)
        }
        findNavController().navigate(
            R.id.action_to_form_producto, bundle)

    }

    private fun searchProductos(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val productos = if (query.isEmpty()) {
                val ordenBusqueda = binding.spinnerSort.selectedItemPosition
                when (ordenBusqueda) {
                    1 -> productoDao.getProductosOrderByDescripcion()
                    2 -> productoDao.getProductosOrderByMarcas()
                    else -> productoDao.getAllProductos()
                }
            } else {
                productoDao.searchProductos(query)
            }
            requireActivity().runOnUiThread {
                mAdapter.submitList(productos)
                updateCountProductos(productos.size)
            }
        }
    }


    private fun loadAllProductos(sortPosition: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val productos = when (sortPosition) {
                1 -> productoDao.getProductosOrderByDescripcion()
                2 -> productoDao.getProductosOrderByMarcas()
                else -> productoDao.getAllProductos()
            }
            requireActivity().runOnUiThread {
                binding.rvProductos.itemAnimator = null

                mAdapter.submitList(productos){
                    (binding.rvProductos.layoutManager as? LinearLayoutManager)
                        ?.scrollToPositionWithOffset(0, 0)
                }

                updateCountProductos(productos.size)
            }
        }
    }

    private fun showDeleteConfirmationDialog(producto: ProductoEntity) {
        val iconDrawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)

        iconDrawable?.let { drawable ->
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), R.color.red)
            )
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(" Eliminar Producto")
            .setMessage("¿Desea eliminar el producto?: ${producto.descripcion}?")
            .setPositiveButton("Sí") { _, _ ->deleteProducto(producto)}
            .setNegativeButton("No", null)
            .create()

        dialog.setIcon(iconDrawable)

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
        }
        dialog.show()
    }

    private fun deleteProducto(producto: ProductoEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            productoDao.deleteProducto(producto)

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Producto ${producto.descripcion} eliminado.", Toast.LENGTH_SHORT).show()

                val currentPosition = binding.spinnerSort.selectedItemPosition
                loadAllProductos(currentPosition)
            }
        }
    }

    private fun updateCountProductos(count: Int) {
        binding.tvProductoCount.text = "$count Producto(s) encontrado(s)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}