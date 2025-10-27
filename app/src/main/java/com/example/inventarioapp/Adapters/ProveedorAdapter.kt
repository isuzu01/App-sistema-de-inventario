package com.example.inventarioapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.ItemRvProveedorBinding
import com.example.inventarioapp.listeners.OnClickListenerProv
import com.example.inventarioapp.models.Proveedor

class ProveedorAdapter(
    var proveedores: MutableList<Proveedor>,
    var listener: OnClickListenerProv
):RecyclerView.Adapter<ProveedorAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_proveedor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proveedor = proveedores.get(position)

        with(holder){
            setListener(proveedor)
            binding.tvNomProveedor.text = proveedor.nomProveedor

            binding.ibtnEmail.setOnClickListener {
                listener.onClickEmail(proveedor.correo)
            }
            binding.ibtnTelefono.setOnClickListener {
                listener.onClickTelefono(proveedor.telef)
            }

            //binding.tvCorreo.text = proveedor.correo
            //binding.tvTelefono.text = proveedor.telef
        }
    }

    override fun getItemCount(): Int= proveedores.size

    fun addProveedor(proveedor: Proveedor){
        proveedores.add(proveedor)
        notifyDataSetChanged()
    }

    fun setProveedoresList(proveedores: MutableList<Proveedor>){
        this.proveedores = proveedores
        notifyDataSetChanged()
    }

    fun updateProveedor(proveedor: Proveedor){
        val index = proveedores.indexOf(proveedor)
        if(index != -1){
            proveedores.set(index, proveedor)
            notifyDataSetChanged()
        }

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemRvProveedorBinding.bind(view)

        fun setListener(proveedor: Proveedor){

            binding.root.setOnClickListener {
                listener.onClickProv(proveedor)
            }
        }
    }
}