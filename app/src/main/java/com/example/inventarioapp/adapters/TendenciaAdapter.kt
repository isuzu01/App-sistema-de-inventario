package com.example.inventarioapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.inventarioapp.R
import com.example.inventarioapp.entity.Tendencia

class TendenciaAdapter(private val tendencias: List<Tendencia>) :
    RecyclerView.Adapter<TendenciaAdapter.TendenciaViewHolder>() {

    class TendenciaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.iv_tendencia_imagen)
        val titulo: TextView = view.findViewById(R.id.tv_tendencia_titulo)
        val fuente: TextView = view.findViewById(R.id.tv_tendencia_fuente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TendenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tendencia, parent, false)
        return TendenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TendenciaViewHolder, position: Int) {
        val tendencia = tendencias[position]

        holder.titulo.text = tendencia.titulo
        holder.fuente.text = tendencia.fuente
        //holder.imagen.setImageResource(tendencia.imagenResId)

        Glide.with(holder.itemView.context)
            .load(tendencia.imagenResId)
            .centerCrop()
            .placeholder(R.drawable.ic_photo)
            .error(R.drawable.ic_photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(600, 400)
            .into(holder.imagen)

    }

    override fun getItemCount() = tendencias.size
}