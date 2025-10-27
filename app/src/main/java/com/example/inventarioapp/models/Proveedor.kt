package com.example.inventarioapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Proveedor(
    @PrimaryKey(autoGenerate = true)
    var id: Long =0,
    var nomProveedor: String = "provedor prueba",
    var correo: String="",
    var telef: String =""
)
