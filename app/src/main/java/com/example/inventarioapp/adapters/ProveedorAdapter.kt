package com.example.inventarioapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioapp.databinding.ItemRvProveedorBinding
import com.example.inventarioapp.entity.ProveedorEntity

class ProveedorAdapter(
    private val onClick: (Long) -> Unit,
    private val onLongClick: (ProveedorEntity) -> Boolean) :
    ListAdapter<ProveedorEntity, ProveedorAdapter.ViewHolder>(ProveedorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvProveedorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proveedor = getItem(position)
        holder.bind(proveedor)

        holder.binding.btnActionEdit.setOnClickListener {
            onClick(proveedor.id)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(proveedor)
            true
        }
    }

    inner class ViewHolder(val binding: ItemRvProveedorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(proveedor: ProveedorEntity) {
            binding.tvNombreEmpresa.text = proveedor.nombreEmpresa
            binding.tvCorreoProveedor.text = proveedor.correo
        }
    }
}
class ProveedorDiffCallback : DiffUtil.ItemCallback<ProveedorEntity>() {
    override fun areItemsTheSame(oldItem: ProveedorEntity, newItem: ProveedorEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProveedorEntity, newItem: ProveedorEntity): Boolean {
        return oldItem == newItem
    }
}
