package com.cchavez.notificaciones_push

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var textoTokenGenerado: TextView
    private lateinit var botonObtenerToken: Button
    private lateinit var botonCopiarToken: Button
    private lateinit var spinnerGrupos: Spinner
    private lateinit var botonSuscribir: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoTokenGenerado = findViewById(R.id.txtTokenGenerado)
        botonObtenerToken = findViewById(R.id.botonObtenerToken)
        botonCopiarToken = findViewById(R.id.botonCopiarToken)
        spinnerGrupos = findViewById(R.id.spinnerGrupos)
        botonSuscribir = findViewById(R.id.botonSuscribir)

        // Configurar el spinner con opciones de grupos
        val grupos = arrayOf("general", "marketing", "noticias", "alertas", "actualizaciones")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, grupos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrupos.adapter = adaptador

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        botonObtenerToken.setOnClickListener {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tarea ->
                    if (tarea.isSuccessful) {
                        val token = tarea.result
                        textoTokenGenerado.text = token
                    } else {
                        textoTokenGenerado.text = "Error al obtener el token: ${tarea.exception?.message}"
                        Log.e("FCM", "Error al obtener el token", tarea.exception)
                    }
                }
            } catch (e: Exception) {
                textoTokenGenerado.text = "Excepción: ${e.message}"
                Log.e("FCM", "Excepción al obtener el token", e)
            }
        }

        botonCopiarToken.setOnClickListener {
            val token = textoTokenGenerado.text.toString()
            if (token.isNotEmpty() && token != "[TOKEN GENERADO]" && !token.startsWith("Error") && !token.startsWith(
                    "Excepción"
                )
            ) {
                val portapapeles = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val datos = ClipData.newPlainText("Token FCM", token)
                portapapeles.setPrimaryClip(datos)
                Toast.makeText(this, "Token copiado al portapapeles", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Primero obtén un token válido", Toast.LENGTH_SHORT).show()
            }
        }

        botonSuscribir.setOnClickListener {
            val grupoSeleccionado = spinnerGrupos.selectedItem.toString()
            suscribirseAGrupo(grupoSeleccionado)
        }
    }

    private fun suscribirseAGrupo(nombreGrupo: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(nombreGrupo)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    Toast.makeText(this, "Suscrito al grupo: $nombreGrupo", Toast.LENGTH_SHORT).show()
                    Log.d("FCM", "Suscrito exitosamente al tema: $nombreGrupo")
                } else {
                    Toast.makeText(this, "Error al suscribirse al grupo: $nombreGrupo", Toast.LENGTH_SHORT).show()
                    Log.e("FCM", "Error al suscribirse al tema", tarea.exception)
                }
            }
    }

    override fun onRequestPermissionsResult(codigoSolicitud: Int, permisos: Array<out String>, resultados: IntArray) {
        super.onRequestPermissionsResult(codigoSolicitud, permisos, resultados)
        if (codigoSolicitud == 1) {
            if (resultados.isNotEmpty() && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM", "Permiso de notificaciones concedido")
            } else {
                Log.d("FCM", "Permiso de notificaciones denegado")
            }
        }
    }
}