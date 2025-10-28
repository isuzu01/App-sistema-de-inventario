package com.example.inventarioapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.inventarioapp.entity.ProveedorEntity

@Dao
interface ProveedorDao {
    @Query("SELECT * FROM Proveedor ORDER BY id ASC")
    fun getAllProveedores(): List<ProveedorEntity>

    @Query("SELECT * FROM Proveedor WHERE nombreEmpresa LIKE :query || '%' OR correo LIKE :query || '%'")
    fun searchProveedores(query: String): List<ProveedorEntity>

    @Query("SELECT * FROM Proveedor ORDER BY nombreEmpresa ASC")
    fun getProveedoresOrderByNombreEmpresa(): List<ProveedorEntity>

    @Query("SELECT * FROM Proveedor ORDER BY correo ASC")
    fun getProveedoresOrderByCorreo(): List<ProveedorEntity>

    @Query("SELECT * FROM Proveedor WHERE id = :proveedorId")
    fun getProveedorById(proveedorId: Long): ProveedorEntity?

    @Insert
    fun insertProveedor(proveedorEntity: ProveedorEntity): Long

    @Update
    fun updateProveedor(proveedorEntity: ProveedorEntity)

    @Delete
    fun deleteProveedor(proveedorEntity: ProveedorEntity)
}