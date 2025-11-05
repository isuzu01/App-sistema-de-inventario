package com.example.inventarioapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventarioapp.databinding.ItemRvProductoBinding
import com.example.inventarioapp.entity.ProductoEntity

class ProductoAdapter(
    private val onClick: (Long) -> Unit,
    private val onLongClick: (ProductoEntity) -> Boolean
) :
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

        holder.binding.cvProducto.setOnLongClickListener {
            onLongClick(producto)
            true
        }
    }

    inner class ViewHolder(val binding: ItemRvProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: ProductoEntity) {
            binding.tvIdProducto.text = producto.id.toString()
            binding.tvDescripcion.text = producto.descripcion
            binding.tvMarca.text = producto.marca
            binding.tvModelo.text = producto.modelo
            binding.tvPrecio.text = "S/.${producto.precio}"
            binding.tvStock.text = producto.stock.toString()
            binding.tvNomProveedor.text = producto.nomProveedor
            binding.tvCategoria.text = producto.nomCategoria

            if (!producto.imagenUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(producto.imagenUrl)
                    .centerCrop()
                    .into(binding.imgPhoto)
            } else {
                binding.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
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