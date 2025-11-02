package com.example.inventarioapp.views.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.ActivityMainBinding
import com.example.inventarioapp.views.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "GeminysLab"

        setupNavegacion()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                mostrarDialogoUsuario()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoUsuario() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nombreUsuario = prefs.getString("usuario_nombre", "Usuario") ?: "Usuario"

        val dialogView = layoutInflater.inflate(R.layout.dialog_user_profile, null)
        val tvUserName = dialogView.findViewById<android.widget.TextView>(R.id.tvUserName)
        val btnCerrarSesion = dialogView.findViewById<android.widget.Button>(R.id.btnCerrarSesion)

        tvUserName.text = "Hola, $nombreUsuario"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun cerrarSesion() {
        // Limpiar datos de sesión
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Volver al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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