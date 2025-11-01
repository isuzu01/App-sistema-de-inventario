package com.example.inventarioapp.views.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventarioapp.databinding.ActivityLoginBinding
import com.example.inventarioapp.views.splashscreen.SplashScreenActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupLogin()
    }

    private fun setupLogin() {
        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validación básica
            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí puedes agregar validación más compleja después si quieres
            // Por ahora solo ingresa y va al Splash
            Toast.makeText(this, "¡Bienvenido $usuario!", Toast.LENGTH_SHORT).show()

            // Ahora va al SplashScreen después del login
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}