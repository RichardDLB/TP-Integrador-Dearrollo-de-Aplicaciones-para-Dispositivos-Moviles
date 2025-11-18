package com.example.ahorramaxlogin

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ahorramaxlogin.databinding.ActivityMainBinding

/*
 * Esta es tu ACTIVIDAD DE LOGIN (la primera que se abre)
 */
class MainActivity : AppCompatActivity() {

    // 1. Declarar la variable de ViewBinding
    // Esta variable se conecta con tu archivo 'activity_main.xml'
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. "Inflar" el layout y conectar el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setBackgroundColor(Color.WHITE)
        // 3. Configurar el botón "Ingresar"
        // Usa el ID que pusiste en tu XML (btnIngresar)
        binding.btnIngresar.setOnClickListener {

            val intent = Intent(this, OffersActivity::class.java)

            // 5. Iniciar la actividad de ofertas
            startActivity(intent)

            // 6. (Opcional) Cerrar esta actividad (Login) para que el usuario
            // no pueda "volver" a ella presionando el botón de atrás.
            finish()
        }

        // 7. (Opcional) Configurar el botón "Registrar"
        // Asumo que el ID en tu XML es 'btnRegistrar'
        binding.btnRegistrar.setOnClickListener {

            // Crear el "Intent" para abrir la pantalla de registro
            val intent = Intent(this, RegistroActivity::class.java)

            // Iniciar la actividad de registro
            startActivity(intent)
        }
    }
}