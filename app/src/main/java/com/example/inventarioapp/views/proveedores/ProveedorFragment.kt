package com.example.inventarioapp.views.proveedores

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioapp.Adapters.ProveedorAdapter
import com.example.inventarioapp.InventorioApplication
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.AlertDialogProveedorBinding
import com.example.inventarioapp.databinding.FragmentProveedorBinding
import com.example.inventarioapp.listeners.OnClickListenerProv
import com.example.inventarioapp.models.Proveedor

class ProveedorFragment : Fragment(R.layout.fragment_proveedor), OnClickListenerProv {

    private lateinit var binding: FragmentProveedorBinding
    private lateinit var mAdapter: ProveedorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProveedorBinding.bind(view)

        setupRecycleView()


        binding.ibtnAdd.setOnClickListener {
            alertDialogAddUpdate("Add")
        }
    }


    private fun setupRecycleView() {
        binding.rvProveedores.layoutManager = LinearLayoutManager(requireActivity())
        mAdapter = ProveedorAdapter(mutableListOf(),this)
        binding.rvProveedores.adapter = mAdapter
        getProveedores()
    }

    private fun alertDialogAddUpdate(accion: String){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val bindingDialog = AlertDialogProveedorBinding.inflate(inflater)
        builder.setView(bindingDialog.root)

        builder.setCancelable(false)

        if(accion == "add"){
            builder.setTitle("AgregarProducto")
        }

        val etNomProveedor = bindingDialog.etNomProveedor
        val etCorreo = bindingDialog.etEmail
        val etTelefono = bindingDialog.etTelefono

        builder.setPositiveButton("Guardar") {_, _ ->

            val proveedor = Proveedor(
                nomProveedor = etNomProveedor.text.toString().trim(),
                correo = etCorreo.text.toString().trim(),
                telef = etTelefono.text.toString().trim()
            )
            Thread {
                InventorioApplication.database.proveedorDao().addProveedor(proveedor)
                getProveedores() // refrescar lista
            }.start()
            mAdapter.addProveedor(proveedor)

            if(accion == "update") {
                val proveedor = Proveedor(
                    nomProveedor = etNomProveedor.text.toString().trim(),
                    correo = etCorreo.text.toString().trim(),
                    telef = etTelefono.text.toString().trim()
                )
                Thread {
                    InventorioApplication.database.proveedorDao().updateProveedor(proveedor)
                }.start()
                mAdapter.updateProveedor(proveedor)
            }

        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }


        builder.create().show()
    }

    private fun getProveedores() {
        Thread{
            val proveedores = InventorioApplication.database.proveedorDao().getAllProveedores()
            requireActivity().runOnUiThread {
                mAdapter.setProveedoresList(proveedores)
            }
        }.start()
    }

    override fun onClickProv(proveedor: Proveedor) {
        alertDialogAddUpdate("update")
    }

    override fun onClickEmail(correo: String) {
        val to = arrayOf(correo)
        val cc = arrayOf("")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_CC, cc)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aqui tu mensaje")

        try {
            requireContext().startActivity(Intent.createChooser(emailIntent, "Enviar email . . ."))
        }catch (ex: ActivityNotFoundException){
            //
        }
    }

    override fun onClickTelefono(telefono: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel: $telefono")
        requireContext().startActivity(intent)
    }


}