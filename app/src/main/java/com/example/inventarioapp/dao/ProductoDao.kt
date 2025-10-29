package com.example.inventarioapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.inventarioapp.entity.ProductoEntity

@Dao
interface ProductoDao {

    @Query("SELECT * FROM Producto  ORDER BY id ASC")
    fun getAllProductos(): List<ProductoEntity>

    @Query("SELECT * FROM Producto WHERE id = :productoId")
    fun getProductoById(productoId: Long): ProductoEntity?

    @Query("SELECT * FROM Producto WHERE descripcion LIKE :query || '%' OR id LIKE :query || '%'")
    fun searchProductos(query: String): List<ProductoEntity>
    @Query("SELECT * FROM Producto ORDER BY descripcion ASC")
    fun getProductosOrderByDescripcion(): List<ProductoEntity>

    @Query("SELECT * FROM Producto ORDER BY marca ASC")
    fun getProductosOrderByMarcas(): List<ProductoEntity>

    @Insert
    fun insertProducto(productoEntity: ProductoEntity):Long

    @Update
    fun updateProducto(productoEntity: ProductoEntity)

    @Delete
    fun deleteProducto(productoEntity: ProductoEntity)
}