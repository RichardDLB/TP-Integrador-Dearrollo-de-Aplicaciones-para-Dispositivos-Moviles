package com.example.ahorramaxlogin

// --- Importaciones necesarias ---
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ahorramaxlogin.RegistroActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Obtener referencias a los componentes del layout (Usando cast explícito para evitar errores de tipo)
        val campoUsuario = findViewById(R.id.etUsuario) as EditText
        val campoContrasena = findViewById(R.id.etContrasena) as EditText
        val botonIngresar = findViewById(R.id.btnIngresar) as Button
        val botonRegistrar = findViewById(R.id.btnRegistrar) as Button

        // 2. Acción del botón Ingresar
        botonIngresar.setOnClickListener {
            verificarCredenciales(campoUsuario, campoContrasena)
        }

        // 3. Acción del botón Registrar (Navegación)
        botonRegistrar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // 4. Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // --- FUNCIONES AUXILIARES ---

    private fun mostrarMensaje(contexto: Context, mensaje: String) {
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
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

    private fun verificarCredenciales(usuario: EditText, contrasena: EditText) {
        val usuarioIngresado = usuario.text.toString()
        val contrasenaIngresada = contrasena.text.toString()

        // Cargar las credenciales guardadas (SharedPreferences)
        val prefs = getSharedPreferences("AhorraMaxPrefs", Context.MODE_PRIVATE)
        val usuarioGuardado = prefs.getString("USUARIO_GUARDADO", null)
        val contrasenaGuardada = prefs.getString("CONTRASENA_GUARDADA", null)

        if (usuarioIngresado.isEmpty() || contrasenaIngresada.isEmpty()) {
            mostrarMensaje(this, "⚠️ Por favor, complete ambos campos.")
            vibrar()
            return
        }

        if (usuarioGuardado == null) {
            mostrarMensaje(this, "❌ No hay usuarios registrados. Regístrese.")
            vibrar()
            return
        }

        // Comparar credenciales
        if (usuarioIngresado == usuarioGuardado && contrasenaIngresada == contrasenaGuardada) {
            mostrarMensaje(this, "✅ Ingreso exitoso. ¡Bienvenido!")
            // TODO: Agregar Intent a la pantalla principal
        } else {
            mostrarMensaje(this, "❌ Usuario o Contraseña incorrecta.")
            vibrar()
        }
    }
}