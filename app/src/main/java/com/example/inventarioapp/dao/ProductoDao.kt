package com.example.inventarioapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.inventarioapp.entity.Producto

@Dao
interface ProductoDao {

    @Query("SELECT * FROM Producto")
    fun getAllProductos(): MutableList<Producto>

    @Insert
    fun addProducto(producto: Producto)

    @Update
    fun updateProducto(producto: Producto)

    @Delete
    fun deleteProducto(producto: Producto)
}