package com.cchavez.s13_proyectoandroid_php_mysql_parte3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cchavez.s13_proyectoandroid_php_mysql_parte3.clases.Configuraciones
import org.json.JSONObject
import com.android.volley.Request
import java.util.regex.Pattern

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var buttonGuardar: Button

    companion object {
        const val MAX_NOMBRE_LENGTH = 50
        const val MIN_NOMBRE_LENGTH = 3
        const val MAX_TELEFONO_LENGTH = 15
        const val MIN_TELEFONO_LENGTH = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_contacto_activity)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        buttonGuardar = findViewById(R.id.buttonGuardar)

        buttonGuardar.isEnabled = false

        setupTextWatchers()

        buttonGuardar.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val telefono = editTextTelefono.text.toString().trim()

            if (validateInputs(nombre, telefono)) {
                agregarContacto(nombre, telefono)
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        editTextNombre.addTextChangedListener(textWatcher)
        editTextTelefono.addTextChangedListener(textWatcher)
    }

    private fun validateForm() {
        val nombre = editTextNombre.text.toString().trim()
        val telefono = editTextTelefono.text.toString().trim()

        buttonGuardar.isEnabled = isNombreValid(nombre) && isTelefonoValid(telefono)
    }

    private fun validateInputs(nombre: String, telefono: String): Boolean {
        if (!isNombreValid(nombre)) {
            showValidationError(when {
                nombre.isEmpty() -> "El nombre no puede estar vacío"
                nombre.length < MIN_NOMBRE_LENGTH -> "El nombre debe tener al menos $MIN_NOMBRE_LENGTH caracteres"
                nombre.length > MAX_NOMBRE_LENGTH -> "El nombre no puede exceder $MAX_NOMBRE_LENGTH caracteres"
                !isValidNameFormat(nombre) -> "El nombre solo debe contener letras y espacios"
                else -> "El nombre no es válido"
            })
            return false
        }

        if (!isTelefonoValid(telefono)) {
            showValidationError(when {
                telefono.isEmpty() -> "El teléfono no puede estar vacío"
                telefono.length < MIN_TELEFONO_LENGTH -> "El teléfono debe tener al menos $MIN_TELEFONO_LENGTH dígitos"
                telefono.length > MAX_TELEFONO_LENGTH -> "El teléfono no puede exceder $MAX_TELEFONO_LENGTH dígitos"
                !isValidPhoneFormat(telefono) -> "El teléfono solo debe contener números, + y -"
                else -> "El teléfono no es válido"
            })
            return false
        }

        return true
    }

    private fun isNombreValid(nombre: String): Boolean {
        return nombre.isNotEmpty() &&
                nombre.length >= MIN_NOMBRE_LENGTH &&
                nombre.length <= MAX_NOMBRE_LENGTH &&
                isValidNameFormat(nombre)
    }

    private fun isTelefonoValid(telefono: String): Boolean {
        return telefono.isNotEmpty() &&
                telefono.length >= MIN_TELEFONO_LENGTH &&
                telefono.length <= MAX_TELEFONO_LENGTH &&
                isValidPhoneFormat(telefono)
    }

    private fun isValidNameFormat(nombre: String): Boolean {
        val namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
        return namePattern.matcher(nombre).matches()
    }

    private fun isValidPhoneFormat(telefono: String): Boolean {
        val phonePattern = Pattern.compile("^[0-9+\\-]+$")
        return phonePattern.matcher(telefono).matches()
    }

    private fun showValidationError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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