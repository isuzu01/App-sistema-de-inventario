package com.example.inventarioapp.listeners

import com.example.inventarioapp.entity.ProveedorEntity

interface OnClickListenerProv {

    fun onClickProv(proveedorEntity: ProveedorEntity)
    fun onClickEmail(correo: String)
    fun onClickTelefono(telefono: String)
}