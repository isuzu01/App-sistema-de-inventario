package com.example.inventarioapp.views.proveedores


import android.os.Bundle
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
import com.example.inventarioapp.R
import com.example.inventarioapp.adapters.ProveedorAdapter
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProveedorBinding
import com.example.inventarioapp.entity.ProveedorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProveedorFragment : Fragment(R.layout.fragment_proveedor){

    private var _binding: FragmentProveedorBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: ProveedorAdapter

    private val proveedorDao: ProveedorDao by lazy {
        InventarioDatabase.getInstance(requireContext()).proveedorDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProveedorBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar la base de datos
        setupRecyclerView()
        setupSpinner()
        setupSearchListener()
        setupListeners()

        parentFragmentManager.setFragmentResultListener(
            "proveedor_actualizar",
            viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean("actualizar", false)) {
                val currentPosition = binding.spinnerSort.selectedItemPosition
                loadAllProveedores(currentPosition)
                Toast.makeText(requireContext(), "Lista de proveedores actualizada.", Toast.LENGTH_SHORT).show()
            }
        }

        loadAllProveedores(0)
    }

    private fun setupSpinner() {
        val adapterSpinner = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = adapterSpinner

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                loadAllProveedores(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = ProveedorAdapter(
            onClick = { proveedorId: Long -> onProveedorClick(proveedorId) },
            onLongClick = { proveedor: ProveedorEntity ->showDeleteConfirmationDialog(proveedor)
                true
            }
        )
        binding.recyclerViewProveedores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchProveedores(query)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupListeners() {
        binding.fabAddProveedor.setOnClickListener {
            findNavController().navigate(R.id.action_to_add_proveedor) //========================
        }
    }

    private fun searchProveedores(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val proveedores = if (query.isEmpty()) {
                val ordenBusqueda = binding.spinnerSort.selectedItemPosition
                when (ordenBusqueda) {
                    1 -> proveedorDao.getProveedoresOrderByNombreEmpresa()
                    2 -> proveedorDao.getProveedoresOrderByCorreo()
                    else -> proveedorDao.getAllProveedores()
                }
            } else {
                proveedorDao.searchProveedores("%$query%")
            }
            requireActivity().runOnUiThread {
                mAdapter.submitList(proveedores)
                updateCountProveedores(proveedores.size)
            }
        }
    }

    private fun onProveedorClick(proveedorId: Long) {
        //  navegar usando navcontroller
        val bundle = Bundle().apply {
                putLong("PROVEEDOR_ID", proveedorId)
            }
        findNavController().navigate(R.id.action_to_edit_proveedor, bundle)
    }


    private fun loadAllProveedores(sortPosition: Int) {
        Thread {
            val proveedores = when (sortPosition) {
                1 -> proveedorDao.getProveedoresOrderByNombreEmpresa()
                2 -> proveedorDao.getProveedoresOrderByCorreo()
                else -> proveedorDao.getAllProveedores()
            }
            requireActivity().runOnUiThread {
                mAdapter.submitList(proveedores)
                updateCountProveedores(proveedores.size)
            }
        }.start()
    }


    private fun showDeleteConfirmationDialog(proveedor: ProveedorEntity) {
        val iconDrawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)

        iconDrawable?.let { drawable ->
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), R.color.red)
            )
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(" Eliminar Proveedor")
            .setMessage("¿Desea eliminar al proveedor: ${proveedor.nombreEmpresa}?")
            .setPositiveButton("Sí") { _, _ ->deleteProveedor(proveedor)}
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

    private fun deleteProveedor(proveedor: ProveedorEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            proveedorDao.deleteProveedor(proveedor)

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Proveedor ${proveedor.nombreEmpresa} eliminado.", Toast.LENGTH_SHORT).show()

                val currentPosition = binding.spinnerSort.selectedItemPosition
                loadAllProveedores(currentPosition)
            }
        }
    }

    private fun updateCountProveedores(count: Int) {
        binding.tvProveedorCount.text = "$count proveedor(es) encontrado(s)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}