package com.example.inventarioapp

import android.app.Application
import androidx.room.Room
import com.example.inventarioapp.database.InventarioDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class InventorioApplication: Application() {
    companion object {
        //lateinit var database: InventarioDatabase
        lateinit var instance: InventorioApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

       /* database = Room.databaseBuilder(this,
            InventarioDatabase::class.java,
            "InventarioDatabase")
            .fallbackToDestructiveMigration()
            .build()

        */
    }
}