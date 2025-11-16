package com.example.ahorramaxlogin

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Usamos el cast explícito aquí también
        val campoUsuario = findViewById(R.id.etUsuarioRegistro) as EditText
        val campoContrasena = findViewById(R.id.etContrasenaRegistro) as EditText
        val botonGuardar = findViewById(R.id.btnGuardarRegistro) as Button

        botonGuardar.setOnClickListener {
            val usuario = campoUsuario.text.toString()
            val contrasena = campoContrasena.text.toString()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                mostrarMensaje("Por favor, complete todos los campos.")
                vibrar()
                return@setOnClickListener
            }

            if (validarContrasena(contrasena)) {
                guardarCredenciales(usuario, contrasena)
                mostrarMensaje("¡Usuario registrado con éxito! Volviendo al Login.")
                finish()
            } else {
                mostrarMensaje("Error: La contraseña debe tener 8+ letras y 1 mayúscula.")
                vibrar()
            }
        }
    }

    // --- LÓGICA DE VALIDACIÓN Y PERSISTENCIA ---

    private fun validarContrasena(pass: String): Boolean {
        val tieneLargo = pass.length >= 8
        val tieneMayuscula = pass.any { it.isUpperCase() }
        return tieneLargo && tieneMayuscula
    }

    private fun guardarCredenciales(usuario: String, contrasena: String) {
        val prefs = getSharedPreferences("AhorraMaxPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("USUARIO_GUARDADO", usuario)
        editor.putString("CONTRASENA_GUARDADA", contrasena)
        editor.apply()
    }

    // --- FUNCIONES AUXILIARES ---

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun vibrar() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
}