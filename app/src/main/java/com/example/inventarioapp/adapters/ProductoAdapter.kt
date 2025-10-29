package com.example.inventarioapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioapp.databinding.ItemRvProductoBinding
import com.example.inventarioapp.entity.ProductoEntity

class ProductoAdapter(
    private val onClick: (Long) -> Unit,
    private val onLongClick: (ProductoEntity) -> Boolean) :
    ListAdapter<ProductoEntity, ProductoAdapter.ViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoAdapter.ViewHolder {
        val binding = ItemRvProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = getItem(position)
        holder.bind(producto)

        holder.binding.cvProducto.setOnClickListener {
            onClick(producto.id)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(producto)
            true
        }
    }
    inner class ViewHolder(val binding: ItemRvProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: ProductoEntity) {
            binding.tvDescripcion.text = producto.descripcion + producto.modelo
            binding.tvMarca.text = producto.marca
            binding.tvPrecio.text = producto.precio.toString()
            binding.tvStock.text = producto.stock.toString()
            binding.tvNomProveedor.text = producto.nomProveedor
            binding.tvCategoria.text = producto.nomCategoria

        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<ProductoEntity>() {
        override fun areItemsTheSame(oldItem: ProductoEntity, newItem: ProductoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductoEntity, newItem: ProductoEntity): Boolean {
            return oldItem == newItem
        }
    }


}