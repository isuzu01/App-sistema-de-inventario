package com.example.inventarioapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.entity.ProductoEntity
import com.example.inventarioapp.entity.ProveedorEntity

@Database(
    entities = [
        ProductoEntity::class,
        ProveedorEntity::class
    ], version = 1
)
abstract class InventarioDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun proveedorDao(): ProveedorDao


    companion object {
        @Volatile
        private var INSTANCE: InventarioDatabase? = null

        fun getInstance(context: Context): InventarioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventarioDatabase::class.java,
                    "InventarioDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}