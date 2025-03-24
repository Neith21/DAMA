package com.cchavez.geolocalizacion

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var botonObtenerCoordenadas: Button
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
        textoLatitud = findViewById(R.id.tvLatitude)
        textoLongitud = findViewById(R.id.tvLongitude)

        gestorUbicacion = getSystemService(LOCATION_SERVICE) as LocationManager
        localizacion = Localizacion(textoLatitud, textoLongitud, this)

        botonObtenerCoordenadas.setOnClickListener {
            if (!rastreandoUbicacion) {
                verificarPermisoUbicacion()
                botonObtenerCoordenadas.text = "Detener rastreo"
                rastreandoUbicacion = true
            } else {
                detenerRastreo()
                botonObtenerCoordenadas.text = "Obtener Coordenadas"
                rastreandoUbicacion = false
            }
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

    override fun onPause() {
        super.onPause()
        if (rastreandoUbicacion) {
            detenerRastreo()
            botonObtenerCoordenadas.text = "Obtener Coordenadas"
            rastreandoUbicacion = false
        }
    }

}
