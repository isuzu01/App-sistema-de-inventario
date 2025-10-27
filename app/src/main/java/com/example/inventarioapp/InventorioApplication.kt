package com.example.inventarioapp

import android.app.Application
import androidx.room.Room
import com.example.inventarioapp.database.InventarioDatabase

class InventorioApplication: Application() {
    companion object {
        lateinit var database: InventarioDatabase
    }
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            InventarioDatabase::class.java,
            "InventarioDatabase")
            .build()
    }
}