package com.cchavez.s09_usodelibrerasparageolocalizacin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var botonObtenerCoordenadas: Button
    private lateinit var botonCompartir: Button
    private lateinit var botonVerMapa: Button
    private lateinit var textoLatitud: TextView
    private lateinit var textoLongitud: TextView
    private lateinit var gestorUbicacion: LocationManager
    private val lanzadorSolicitudPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            obtenerUltimaUbicacion()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var localizacion: Localizacion
    private var rastreandoUbicacion = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botonObtenerCoordenadas = findViewById(R.id.btnGetCoordinates)
        botonCompartir = findViewById(R.id.btnShare)
        botonVerMapa = findViewById(R.id.btnShowMap)
        textoLatitud = findViewById(R.id.tvLatitude)
        textoLongitud = findViewById(R.id.tvLongitude)

        gestorUbicacion = getSystemService(LOCATION_SERVICE) as LocationManager
        localizacion = Localizacion(textoLatitud, textoLongitud, this)

        botonObtenerCoordenadas.setOnClickListener {
            if (!rastreandoUbicacion) {
                verificarPermisoUbicacion()
                botonObtenerCoordenadas.text = "DETENER RASTREO"
                rastreandoUbicacion = true
            } else {
                detenerRastreo()
                botonObtenerCoordenadas.text = "OBTENER COORDENADAS"
                rastreandoUbicacion = false
            }
        }

        botonCompartir.setOnClickListener {
            compartirEnWhatsApp()
        }

        botonVerMapa.setOnClickListener {
            mostrarMapa()
        }
    }

    private fun verificarPermisoUbicacion() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                obtenerUltimaUbicacion()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "Se necesita permiso de ubicación para continuar", Toast.LENGTH_LONG).show()
                lanzadorSolicitudPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                lanzadorSolicitudPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun obtenerUltimaUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val ultimaUbicacion = gestorUbicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: gestorUbicacion.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (ultimaUbicacion != null) {
                textoLatitud.text = String.format("%.6f", ultimaUbicacion.latitude)
                textoLongitud.text = String.format("%.6f", ultimaUbicacion.longitude)
                localizacion.setLastLocation(ultimaUbicacion)
            } else {
                textoLatitud.text = "No disponible"
                textoLongitud.text = "No disponible"
                Toast.makeText(this@MainActivity, "No se encontró ubicación reciente", Toast.LENGTH_SHORT).show()
            }

            gestorUbicacion.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0.5f,
                localizacion
            )

            if (gestorUbicacion.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                gestorUbicacion.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    0.5f,
                    localizacion
                )
            }

            Toast.makeText(this, "Rastreo de ubicación iniciado", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            textoLatitud.text = "Error"
            textoLongitud.text = "Error"
            Toast.makeText(this@MainActivity, "Error de seguridad: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            textoLatitud.text = "Error"
            textoLongitud.text = "Error"
            Toast.makeText(this@MainActivity, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detenerRastreo() {
        gestorUbicacion.removeUpdates(localizacion)
        Toast.makeText(this, "Rastreo de ubicación detenido", Toast.LENGTH_SHORT).show()
    }

    private fun compartirEnWhatsApp() {
        val latitud = textoLatitud.text.toString()
        val longitud = textoLongitud.text.toString()

        if (latitud == "00000" || latitud == "No disponible" || latitud == "Error" ||
            longitud == "00000" || longitud == "No disponible" || longitud == "Error") {
            Toast.makeText(this, "Primero debes obtener coordenadas válidas", Toast.LENGTH_SHORT).show()
            return
        }

        val mensaje = "Hola, te adjunto mi ubicación: https://maps.google.com/?q=$latitud,$longitud"

        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, mensaje)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp no está instalado o no se puede abrir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarMapa() {
        val latitud = textoLatitud.text.toString()
        val longitud = textoLongitud.text.toString()

        if (latitud == "00000" || latitud == "No disponible" || latitud == "Error" ||
            longitud == "00000" || longitud == "No disponible" || longitud == "Error") {
            Toast.makeText(this, "Primero debes obtener coordenadas válidas", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra("LATITUD", latitud.toDouble())
                putExtra("LONGITUD", longitud.toDouble())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir el mapa: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (rastreandoUbicacion) {
            detenerRastreo()
            botonObtenerCoordenadas.text = "OBTENER COORDENADAS"
            rastreandoUbicacion = false
        }
    }
}