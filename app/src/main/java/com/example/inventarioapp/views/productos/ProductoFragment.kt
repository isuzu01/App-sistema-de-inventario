package com.example.inventarioapp.views.productos

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioapp.Adapters.ProductoAdapter
import com.example.inventarioapp.InventorioApplication
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.AlertDialogProductoBinding
import com.example.inventarioapp.databinding.FragmentProductoBinding
import com.example.inventarioapp.listeners.OnClickListenerProd
import com.example.inventarioapp.entity.Producto


class ProductoFragment : Fragment(R.layout.fragment_producto), OnClickListenerProd {

    private lateinit var mBinding: FragmentProductoBinding
    private lateinit var mBindingDialog: AlertDialogProductoBinding
    private lateinit var mAdapter: ProductoAdapter

    private var mListaProd: MutableList<Producto> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentProductoBinding.bind(view)

        setupRecycleView()

        mBinding.ibtnAdd.setOnClickListener {
            alertDialogAddUpdate("add")
        }

        mBinding.etBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(editText: Editable?) {
                if (editText.isNullOrEmpty()){
                    getProductos()
                }else{
                    filtrarListaProductos(editText.toString().trim())
                }
            }
        })
    }

    private fun setupRecycleView(){
        mBinding.rvProductos.layoutManager = LinearLayoutManager(requireActivity())
        mAdapter = ProductoAdapter(mutableListOf(), this)
        mBinding.rvProductos.adapter = mAdapter
        getProductos()

    }

// funcion para crear y actualizar

    private  fun alertDialogAddUpdate(accion: String, producto: Producto? = null){
        val  builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        mBindingDialog = AlertDialogProductoBinding.inflate(inflater)
        builder.setView(mBindingDialog.root)

        builder.setCancelable(false)

        if(accion == "add"){
            builder.setTitle("Agregar Producto")
            mBindingDialog.etCodigo.visibility = View.GONE
        }
        else{
            builder.setTitle("Editar Producto")
        }

        val etCodigo = mBindingDialog.etCodigo
        val etDescripcion = mBindingDialog.etDescripcion
        val etMarca = mBindingDialog.etMarca
        val etModelo = mBindingDialog.etModelo
        val etPrecio = mBindingDialog.etPrecio
        val etStock = mBindingDialog.etStock
        val spiNomCategoria = mBindingDialog.spiCategoria
        val spiNomProveedor = mBindingDialog.spiProveedor

        // cargar categorias
        val categorias = listOf("Lptot", "mouse", "teclado", "Otros")
        spiNomCategoria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)

        Thread {

            // Cargar proveedores
            val proveedores = InventorioApplication.database.proveedorDao().getAllProveedores()
            val nombresProveedores = proveedores.map { it.nombreEmpresa }
            requireActivity().runOnUiThread {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresProveedores)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spiNomProveedor.adapter = adapter

                //si es editar mostrar los datos
                if (accion == "update" && producto != null){
                    etCodigo.setText(producto?.id.toString())
                    etCodigo.isEnabled = false
                    etDescripcion.setText(producto.descripcion)
                    etMarca.setText(producto.marca)
                    etModelo.setText(producto.modelo)
                    etPrecio.setText(producto.precio.toString())
                    etStock.setText(producto.stock.toString())

                    // Seleccionar categoría y proveedor
                    val indexCategoria = categorias.indexOf(producto.nomCategoria)
                    if (indexCategoria != -1) spiNomCategoria.setSelection(indexCategoria)

                    // Seleccionar proveedor
                    val indexProveedor = nombresProveedores.indexOf(producto.nomProveedor)
                    if (indexProveedor != -1) spiNomProveedor.setSelection(indexProveedor)
                }
            }
        }.start()


        builder.setPositiveButton("Guardar") { dialog, _ ->
/*
            val producto = Producto(
                id = 0,
                descripcion = etDescripcion.text.toString().trim(),
                marca = etMarca.text.toString().trim(),
                modelo = etModelo.text.toString().trim(),
                precio = etPrecio.text.toString().toDouble(),
                stock = etStock.text.toString().toInt(),
                nomProveedor = spiNomProveedor.selectedItem.toString().trim(),
                nomCategoria = spiNomCategoria.selectedItem.toString().trim()
            )
            Thread {
                if (accion == "add") {
                    InventorioApplication.database.productoDao().addProducto(producto)
                } else {
                    InventorioApplication.database.productoDao().updateProducto(producto)
                }

                requireActivity().runOnUiThread {
                    getProductos()
                }
            }.start()

            */

            when (accion) {
                "add" -> {
                    // Para agregar, crear producto sin ID (se autogenerará)
                    val nuevoProducto = Producto(
                        descripcion = etDescripcion.text.toString().trim(),
                        marca = etMarca.text.toString().trim(),
                        modelo = etModelo.text.toString().trim(),
                        precio = etPrecio.text.toString().toDouble(),
                        stock = etStock.text.toString().toInt(),
                        nomProveedor = spiNomProveedor.selectedItem.toString().trim(),
                        nomCategoria = spiNomCategoria.selectedItem.toString().trim()
                    )

                    Thread {
                        InventorioApplication.database.productoDao().addProducto(nuevoProducto)
                        requireActivity().runOnUiThread {
                            getProductos()
                        }
                    }.start()
                }
                "update" -> {
                    if (producto != null) {
                        // Para actualizar, mantener el ID original
                        val productoActualizado = Producto(
                            id = producto.id, // Mantener el mismo ID
                            descripcion = etDescripcion.text.toString().trim(),
                            marca = etMarca.text.toString().trim(),
                            modelo = etModelo.text.toString().trim(),
                            precio = etPrecio.text.toString().toDouble(),
                            stock = etStock.text.toString().toInt(),
                            nomProveedor = spiNomProveedor.selectedItem.toString().trim(),
                            nomCategoria = spiNomCategoria.selectedItem.toString().trim()
                        )

                        Thread {
                            InventorioApplication.database.productoDao().updateProducto(productoActualizado)
                            requireActivity().runOnUiThread {
                                getProductos()
                            }
                        }.start()
                    }
                }
            }

        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private  fun getProductos(){
        Thread{
            val productos = InventorioApplication.database.productoDao().getAllProductos()
            requireActivity().runOnUiThread {
                mListaProd = productos.toMutableList()
                mAdapter.setProductosList(productos)
            }
        }.start()

    }


    //filtro de busqueda por nombre(descripcion del prod) y id
    private fun filtrarListaProductos(filtro: String) {
        val listaFiltrada = mListaProd.filter { producto ->
            producto.id.toString().contains(filtro, true) ||
                    producto.descripcion.contains(filtro, true)
        }.toMutableList()

        mAdapter.setProductosList(listaFiltrada)
    }


    override fun onClick(producto: Producto) {
        alertDialogAddUpdate("update", producto)
    }

}