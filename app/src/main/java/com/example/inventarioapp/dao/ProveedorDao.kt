package com.example.inventarioapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.inventarioapp.models.Proveedor

@Dao
interface ProveedorDao {
    @Query("SELECT * FROM Proveedor")
    fun getAllProveedores(): MutableList<Proveedor>


    @Insert
    fun addProveedor(proveedor: Proveedor)

    @Update
    fun updateProveedor(proveedor: Proveedor)

    @Delete
    fun deleteProveedor(proveedor: Proveedor)
}