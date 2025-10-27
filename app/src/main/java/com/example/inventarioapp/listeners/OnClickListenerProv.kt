package com.example.inventarioapp.listeners

import com.example.inventarioapp.models.Proveedor

interface OnClickListenerProv {

    fun onClickProv(proveedor: Proveedor)
    fun onClickEmail(correo: String)
    fun onClickTelefono(telefono: String)
}