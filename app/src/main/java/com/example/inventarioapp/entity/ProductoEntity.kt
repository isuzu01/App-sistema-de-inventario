package com.example.inventarioapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Producto")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val descripcion: String,
    var marca: String = "",
    var modelo: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0,
    var nomProveedor: String ="",
    var nomCategoria: String =""
    )
