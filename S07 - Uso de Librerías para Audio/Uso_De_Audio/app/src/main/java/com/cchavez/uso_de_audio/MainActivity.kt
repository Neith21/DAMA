package com.cchavez.uso_de_audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var reproductorMedia: MediaPlayer? = null
    private var fuenteAudioEstablecida = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnReproducir = findViewById<ImageButton>(R.id.imgBoton)
        val btnDetener = findViewById<ImageButton>(R.id.imgBotonStop)
        val btnRemoto = findViewById<Button>(R.id.btnRemoto)
        val btnLocal = findViewById<Button>(R.id.btnLocal)

        reproductorMedia = MediaPlayer()

        btnLocal.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true) {
                    reproductorMedia?.stop()
                }

                reproductorMedia?.reset()
                try {
                    val idRecurso = R.raw.sonido_test
                    reproductorMedia?.setDataSource(
                        this,
                        android.net.Uri.parse("android.resource://$packageName/$idRecurso")
                    )

                    reproductorMedia?.setOnPreparedListener {
                        fuenteAudioEstablecida = true
                        mostrarMensaje("Audio local listo para reproducir")
                    }

                    reproductorMedia?.setOnErrorListener { _, _, _ ->
                        fuenteAudioEstablecida = false
                        mostrarMensaje("Error al cargar el audio local")
                        false
                    }

                    mostrarMensaje("Cargando audio local...")
                    reproductorMedia?.prepare()
                } catch (e: Exception) {
                    mostrarMensaje("Error al acceder al archivo de audio local")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al cargar el audio local")
            }
        }

        btnRemoto.setOnClickListener {
            try {
                if (!hayConexionInternet()) {
                    mostrarMensaje("No hay conexión a internet")
                    return@setOnClickListener
                }

                if (reproductorMedia?.isPlaying == true) {
                    reproductorMedia?.stop()
                }

                reproductorMedia?.reset()
                val urlAudio = "https://tonosmovil.net/wp-content/uploads/tonosmovil.net_himno_champions_league.mp3"
                reproductorMedia?.setDataSource(urlAudio)

                reproductorMedia?.setOnPreparedListener {
                    fuenteAudioEstablecida = true
                    mostrarMensaje("Audio remoto listo para reproducir")
                }

                reproductorMedia?.setOnErrorListener { mp, what, extra ->
                    fuenteAudioEstablecida = false
                    mostrarMensaje("Error al cargar el audio remoto: código $what-$extra")
                    false
                }

                mostrarMensaje("Cargando audio remoto...")
                reproductorMedia?.prepareAsync()
            } catch (e: Exception) {
                mostrarMensaje("Error al configurar el audio remoto: ${e.message}")
                Log.e("MainActivity", "Error de audio remoto", e)
            }
        }

        btnDetener.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true) {
                    reproductorMedia?.stop()
                    reproductorMedia?.reset()
                    fuenteAudioEstablecida = false
                    mostrarMensaje("Reproducción detenida")
                } else {
                    mostrarMensaje("No hay audio reproduciéndose")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al detener el audio")
            }
        }

        btnReproducir.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true) {
                    mostrarMensaje("El audio ya se está reproduciendo")
                } else if (fuenteAudioEstablecida) {
                    reproductorMedia?.start()
                    mostrarMensaje("Reproduciendo audio")
                } else {
                    mostrarMensaje("Seleccione primero un audio (local o remoto)")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al reproducir el audio")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun hayConexionInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo

        @Suppress("DEPRECATION")
        return networkInfo != null && networkInfo.isConnected
    }
}