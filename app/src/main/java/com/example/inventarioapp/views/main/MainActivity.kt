package com.example.inventarioapp.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavegacion()
    }

    private fun setupNavegacion() {
        binding.bottomNavigationView.itemIconTintList = null
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_welcome -> {
                    if (navController.currentDestination?.id != R.id.page_welcome) {
                        navController.navigate(R.id.page_welcome)
                    }
                    true
                }

                R.id.page_productos -> {
                    if (navController.currentDestination?.id != R.id.page_productos) {
                        navController.navigate(R.id.page_productos)
                    }
                    true
                }
                R.id.page_proveedores -> {
                    if (navController.currentDestination?.id != R.id.page_proveedores) {
                        navController.navigate(R.id.page_proveedores)
                    }
                    true
                }
                else -> false
            }
        }
    }

}