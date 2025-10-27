package com.example.inventarioapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.models.Producto
import com.example.inventarioapp.models.Proveedor

@Database(entities = arrayOf(
           Producto::class,
           Proveedor::class
         ), version = 1)
abstract class InventarioDatabase: RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun proveedorDao(): ProveedorDao
}