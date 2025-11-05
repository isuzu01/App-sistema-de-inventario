package com.example.inventarioapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Proveedor")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    //DATOS DE LA EMPRESA
    var nombreEmpresa: String = "",
    var ruc: String = "",
    var esNacional: Boolean = true,
    var direccion: String = "",

    //DATOS contacto
    var nombreContacto: String = "",
    var correo: String = "",
    var telefono: String = ""
) {
    constructor() : this(0, "", "", true, "", "", "", "")
}

