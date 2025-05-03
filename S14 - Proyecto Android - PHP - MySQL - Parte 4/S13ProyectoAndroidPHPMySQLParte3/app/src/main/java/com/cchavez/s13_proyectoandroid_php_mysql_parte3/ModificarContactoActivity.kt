package com.cchavez.s13_proyectoandroid_php_mysql_parte3

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cchavez.s13_proyectoandroid_php_mysql_parte3.clases.Configuraciones
import org.json.JSONObject
import java.util.regex.Pattern

class ModificarContactoActivity : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var buttonGuardar: Button
    private var idContacto: Int = 0

    companion object {
        const val MAX_NOMBRE_LENGTH = 50
        const val MIN_NOMBRE_LENGTH = 3
        const val MAX_TELEFONO_LENGTH = 15
        const val MIN_TELEFONO_LENGTH = 7
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificarcontactoactivity)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        val buttonEliminar = findViewById<Button>(R.id.buttonEliminar)
        val buttonRegresar = findViewById<Button>(R.id.buttonRegresar)

        idContacto = intent.getIntExtra("id_contacto", 0)
        val nombre = intent.getStringExtra("nombre") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""

        editTextNombre.setText(nombre)
        editTextTelefono.setText(telefono)

        setupTextWatchers()

        buttonGuardar.setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString().trim()
            val nuevoTelefono = editTextTelefono.text.toString().trim()

            if (validateInputs(nuevoNombre, nuevoTelefono)) {
                actualizarContacto(idContacto, nuevoNombre, nuevoTelefono)
            }
        }

        buttonEliminar.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        buttonRegresar.setOnClickListener {
            finish()
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

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que deseas eliminar este contacto? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                eliminarContacto(idContacto)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun actualizarContacto(idContacto: Int, nombre: String, telefono: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.URL_WEBSERVICES}?action=actualizarContacto"

        val jsonBody = JSONObject().apply {
            put("id_contacto", idContacto)
            put("nombre", nombre)
            put("telefono", telefono)
        }

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val status = response.getString("status")
                    val message = response.optString("message", "Sin mensaje")

                    if (status == "success") {
                        Toast.makeText(this, "Contacto actualizado con éxito", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
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

    private fun eliminarContacto(idContacto: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.URL_WEBSERVICES}?action=eliminarContacto"
        val jsonBody = JSONObject().apply {
            put("id_contacto", idContacto)
        }

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val status = response.getString("status")
                    val message = response.optString("message", "Sin mensaje")

                    if (status == "success") {
                        Toast.makeText(this, "Contacto eliminado con éxito", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
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