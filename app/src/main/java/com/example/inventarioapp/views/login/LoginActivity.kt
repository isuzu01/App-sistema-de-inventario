package com.example.inventarioapp.views.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventarioapp.databinding.ActivityLoginBinding
import com.example.inventarioapp.utils.Constantes
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
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validación de credenciales
            if (usuario == Constantes.USUARIO_CORRECTO && password == Constantes.PASSWORD_CORRECTO) {

                // Guardar el nombre de usuario en SharedPreferences
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit().apply {
                    putString("usuario_nombre", usuario)
                    putBoolean("is_logged_in", true)
                    apply()
                }

                // Login exitoso
                Toast.makeText(this, "¡Bienvenido $usuario!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Login fallido
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()

                // Opcional: Limpiar campos
                binding.etPassword.text?.clear()
                binding.etPassword.requestFocus()

            }
        }
    }
}