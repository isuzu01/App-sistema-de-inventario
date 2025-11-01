package com.example.inventarioapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.inventarioapp.entity.ProductoEntity

@Dao
interface ProductoDao {

    @Query("SELECT * FROM Producto  ORDER BY id ASC")
    fun getAllProductos(): List<ProductoEntity>

    @Query("SELECT * FROM Producto WHERE descripcion LIKE :query || '%' OR id LIKE :query || '%'")
    fun searchProductos(query: String): List<ProductoEntity>
    @Query("SELECT * FROM Producto ORDER BY descripcion ASC")
    fun getProductosOrderByDescripcion(): List<ProductoEntity>

    @Query("SELECT * FROM Producto ORDER BY marca ASC")
    fun getProductosOrderByMarcas(): List<ProductoEntity>

    @Query("SELECT * FROM Producto WHERE id = :productoId")
    fun getProductoById(productoId: Long): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducto(productoEntity: ProductoEntity):Long

    //
    //total productos
    @Query("SELECT COUNT(id) FROM Producto")
    fun getTotalArticulosCount(): LiveData<Int>

    //sin stock
    @Query("SELECT COUNT(id) FROM Producto WHERE stock = 0")
    fun getProductosSinStockCount(): LiveData<Int>

    //stock bajo
    @Query("SELECT COUNT(id) FROM Producto WHERE stock > 0 AND stock <= 10")
    fun getProductosStockBajoCount(): LiveData<Int>

    //suma unidades de stock de todos los productos
    @Query("SELECT SUM(stock) FROM Producto")
    fun getSumaTotalStockUnits(): LiveData<Int>


    @Update
    fun updateProducto(productoEntity: ProductoEntity)

    @Delete
    fun deleteProducto(productoEntity: ProductoEntity)

    @Query("DELETE FROM Producto")
    fun clearAllProductos()
}