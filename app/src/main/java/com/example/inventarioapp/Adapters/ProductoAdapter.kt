package com.example.inventarioapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.ItemRvProductoBinding
import com.example.inventarioapp.listeners.OnClickListenerProd
import com.example.inventarioapp.entity.Producto

class ProductoAdapter(
    private var productos: MutableList<Producto>,
    private var listener: OnClickListenerProd):
    RecyclerView.Adapter<ProductoAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoAdapter.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_producto, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos.get(position)

        with(holder){
            setListener(producto)
            binding.tvIdProducto.text = producto.id.toString()
            binding.tvDescripcion.text = "Producto: ${producto.descripcion} ${producto.modelo}"
            binding.tvMarca.text = "Marca: ${producto.marca}"
            binding.tvCategoria.text = "Categoria: ${producto.nomCategoria}"
            binding.tvNomProveedor.text = "Proveedor: ${producto.nomProveedor}"
            binding.tvStock.text = "Stock: ${producto.stock}"
            binding.tvPrecio.text = "Precio: ${producto.precio}"

        }

    }

    override fun getItemCount(): Int = productos.size

    fun add(producto: Producto) {
        productos.add(producto)
        notifyDataSetChanged()
    }

    fun setProductosList(productos: MutableList<Producto>){
        this.productos = productos
        notifyDataSetChanged()
    }

    fun  update(producto: Producto){
        val index = productos.indexOfFirst { it.id == producto.id }
        if(index != -1){
            productos.set(index, producto)
            notifyDataSetChanged()
        }
    }

    fun delete(producto: Producto){
        val index = productos.indexOf(producto)
        if(index != -1){
            productos.removeAt(index)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemRvProductoBinding.bind(view)

        fun setListener(producto: Producto){
            binding.root.setOnClickListener {
                listener.onClick(producto)
            }
        }
    }

}