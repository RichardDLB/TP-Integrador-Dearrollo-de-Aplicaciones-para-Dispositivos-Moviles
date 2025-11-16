package com.example.ahorramaxlogin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val campoUsuario = findViewById<EditText>(R.id.etUsuario)
        val campoContrasena = findViewById<EditText>(R.id.etContrasena)
        val botonIngresar = findViewById<Button>(R.id.btnIngresar)
        botonIngresar.setOnClickListener {
            verificarCredenciales(campoUsuario, campoContrasena)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }

private fun mostrarMensaje(contexto: Context, mensaje: String) {
    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
}
private fun verificarCredenciales(usuario: EditText, contrasena: EditText) {
    // Obtenemos los valores de texto de los campos
    val usuarioIngresado = usuario.text.toString()
    val contrasenaIngresada = contrasena.text.toString()

    // Definimos las credenciales válidas (solo para prueba)
    val usuarioValido = "Macaco"
    val contrasenaValida = "12345"

    // Lógica de verificación
    if (usuarioIngresado == usuarioValido && contrasenaIngresada == contrasenaValida) {
        // Si son correctas
        mostrarMensaje(this, "✅ Ingreso exitoso. ¡Bienvenido!")
        // Aquí iría el código para pasar a la siguiente pantalla (Intent)

    } else if (usuarioIngresado.isEmpty() || contrasenaIngresada.isEmpty()) {
        // Si algún campo está vacío
        mostrarMensaje(this, "⚠️ Por favor, complete ambos campos.")

    } else {
        // Si son incorrectas
        mostrarMensaje(this, "❌ Usuario o Contraseña incorrecta.")
    }
}
}