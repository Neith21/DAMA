package com.cchavez.s13_proyectoandroid_php_mysql_parte3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cchavez.s13_proyectoandroid_php_mysql_parte3.clases.Configuraciones
import org.json.JSONObject
import com.android.volley.Request

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextTelefono: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_contacto_activity)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        val buttonGuardar = findViewById<Button>(R.id.buttonGuardar)

        buttonGuardar.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val telefono = editTextTelefono.text.toString()
            if (nombre.isNotEmpty() && telefono.isNotEmpty()) {
                agregarContacto(nombre, telefono)
            } else {
                Toast.makeText(this, "Introduce nombre y teléfono", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarContacto(nombre: String, telefono: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.URL_WEBSERVICES}?action=agregarContacto"

        // Crear el cuerpo JSON
        val jsonBody = JSONObject()
        jsonBody.put("nombre", nombre)
        jsonBody.put("telefono", telefono)

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    // Verificar si "status" existe
                    val status = if (response.has("status") && !response.isNull("status")) response.getString("status") else "error"
                    if (status == "success") {
                        val message = response.optString("message", "Contacto agregado con éxito")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val message = response.optString("message", "Error desconocido al agregar contacto")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al parsear respuesta: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message ?: "Desconocido"}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(jsonRequest)
    }
}
