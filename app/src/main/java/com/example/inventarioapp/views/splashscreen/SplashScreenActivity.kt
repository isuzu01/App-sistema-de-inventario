package com.example.inventarioapp.views.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.inventarioapp.R
import com.example.inventarioapp.databinding.ActivitySplashScreenBinding
import com.example.inventarioapp.utils.Constantes
import com.example.inventarioapp.views.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        Glide.with(this).load(R.drawable.dispositivos).centerCrop().into(binding.ivSplashScreen)

        cambiarPantalla()
    }

    private fun cambiarPantalla() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, Constantes.DURACION_SPLASH_SCREEN)
    }
}