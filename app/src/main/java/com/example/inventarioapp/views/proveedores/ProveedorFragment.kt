package com.example.inventarioapp.views.proveedores


import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioapp.R
import com.example.inventarioapp.adapters.ProveedorAdapter
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.database.InventarioDatabase
import com.example.inventarioapp.databinding.FragmentProveedorBinding
import com.example.inventarioapp.entity.ProveedorEntity
import com.example.inventarioapp.repository.FirebaseProveedorRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProveedorFragment : Fragment(R.layout.fragment_proveedor) {

    private var _binding: FragmentProveedorBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: ProveedorAdapter
    private lateinit var proveedorDao: ProveedorDao

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
        val db = InventarioDatabase.getInstance(requireContext().applicationContext)
        proveedorDao = db.proveedorDao()

        setupRecyclerView()
        setupSpinner()
        setupSearchListener()
        setupListeners()

        parentFragmentManager.setFragmentResultListener(
            "proveedor_actualizar",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("actualizar", false)) {
                val currentPosition = binding.spinnerSort.selectedItemPosition
                loadAllProveedores(currentPosition)
                Toast.makeText(
                    requireContext(),
                    "Lista de proveedores actualizada.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //loadAllProveedores(0)
        loadProveedoresFromFirebase()
        FirebaseProveedorRepository.sincronizarProveedorDesdeFirebase(proveedorDao)
        observarProveedoresEnTiempoReal()
    }

    private fun observarProveedoresEnTiempoReal() {
        FirebaseProveedorRepository.observarProveedoresEnTiempoReal { proveedores ->
            if (!isAdded || _binding == null) return@observarProveedoresEnTiempoReal

            lifecycleScope.launch(Dispatchers.Main) {
                if (_binding == null || !isAdded) {
                    mAdapter.submitList(proveedores)
                    updateCountProveedores(proveedores.size)
                }
            }
        }
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
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                loadAllProveedores(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRecyclerView() {
        mAdapter = ProveedorAdapter(
            onClick = { proveedorId: Long -> onProveedorClick(proveedorId) },
            onLongClick = { proveedor: ProveedorEntity ->
                showDeleteConfirmationDialog(proveedor)
                true
            }
        )
        binding.recyclerViewProveedores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchProveedores(query)
            }

        })
    }

    private fun setupListeners() {
        binding.fabAddProveedor.setOnClickListener {
            findNavController().navigate(
                R.id.action_to_form_proveedor,
                Bundle().apply { putLong("proveedorId", 0L) })
        }
    }

    private fun onProveedorClick(proveedorId: Long) {

        val bundle = Bundle().apply {
            putLong("proveedorId", proveedorId)
        }
        findNavController().navigate(
            R.id.action_to_form_proveedor, bundle
        )

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
                proveedorDao.searchProveedores(query)
            }
            if (!isAdded || _binding == null) return@launch
            requireActivity().runOnUiThread {
                if (_binding != null && isAdded) {
                    mAdapter.submitList(proveedores)
                    updateCountProveedores(proveedores.size)
                }
            }
        }
    }

    private fun loadProveedoresFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("proveedores")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || _binding == null) return

                val proveedoresList = mutableListOf<ProveedorEntity>()
                for (provSnapshot in snapshot.children) {
                    val proveedor = provSnapshot.getValue(ProveedorEntity::class.java)
                    proveedor?.let { proveedoresList.add(it) }
                }

                if (_binding != null && isAdded) {
                    mAdapter.submitList(proveedoresList)
                    updateCountProveedores(proveedoresList.size)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (!isAdded || _binding == null) return
                Toast.makeText(
                    requireContext(),
                    "Error al cargar datos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun loadAllProveedores(sortPosition: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val proveedores = when (sortPosition) {
                1 -> proveedorDao.getProveedoresOrderByNombreEmpresa()
                2 -> proveedorDao.getProveedoresOrderByCorreo()
                else -> proveedorDao.getAllProveedores()
            }
            if (!isAdded || _binding == null) return@launch
            requireActivity().runOnUiThread {
                mAdapter.submitList(proveedores)
                updateCountProveedores(proveedores.size)
            }
        }
    }


    private fun showDeleteConfirmationDialog(proveedor: ProveedorEntity) {
        val iconDrawable =
            ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)

        iconDrawable?.let { drawable ->
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(
                wrappedDrawable, ContextCompat.getColor(requireContext(), R.color.red)
            )
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(" Eliminar Proveedor")
            .setMessage("¿Desea eliminar al proveedor: ${proveedor.nombreEmpresa}?")
            .setPositiveButton("Sí") { _, _ -> deleteProveedor(proveedor) }
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
            try {
                FirebaseProveedorRepository.eliminarProveedorFirebaseYRoom(proveedorDao, proveedor)
                if (!isAdded || _binding == null) return@launch

                withContext(Dispatchers.Main) {
                    if (_binding != null && isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Proveedor ${proveedor.nombreEmpresa} eliminado.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val currentPosition = binding.spinnerSort.selectedItemPosition
                        loadAllProveedores(currentPosition)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateCountProveedores(count: Int) {
        if (_binding == null || !isAdded) return
        binding.tvProveedorCount.text = "$count proveedor(es) encontrado(s)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}