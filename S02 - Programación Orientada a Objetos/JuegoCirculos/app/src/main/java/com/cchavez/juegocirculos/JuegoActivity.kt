package com.cchavez.juegocirculos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class JuegoActivity : AppCompatActivity() {
    private lateinit var cuadrilla: GridLayout
    private lateinit var textoNivel: TextView
    private lateinit var textoVidas: TextView
    private lateinit var textoTiempo: TextView
    private lateinit var circulos: List<View>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var contadorTiempo: CountDownTimer

    private val manejador = Handler(Looper.getMainLooper())
    private var puntaje = 0
    private var nivelActual = 1
    private var circulosPorNivel = 5
    private var circulosVerdes = 0
    private var vidas = 3
    private var tiempoRestante = 30
    private var tiempoCirculoVerde = 1000L
    private val velocidadBase = 1000L
    private val nivelMaximo = 5
    private var puntosParaNivel = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        inicializarVistas()
        inicializarSonidos()
        iniciarNivel()
    }

    private fun inicializarVistas() {
        cuadrilla = findViewById(R.id.gridLayout)
        textoNivel = findViewById(R.id.levelText)
        textoVidas = findViewById(R.id.vidasText)
        textoTiempo = findViewById(R.id.tiempoText)
        circulos = List(12) { crearCirculo() }
        circulos.forEach { cuadrilla.addView(it) }
    }

    private fun inicializarSonidos() {
        mediaPlayer = MediaPlayer.create(this, R.raw.click_success)
    }

    private fun crearCirculo(): View {
        return View(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            background = ContextCompat.getDrawable(this@JuegoActivity, R.drawable.circle_red)
            setOnClickListener { alHacerClickEnCirculo(this) }
        }
    }

    private fun iniciarNivel() {
        textoNivel.text = "Nivel $nivelActual"
        textoVidas.text = "Vidas: $vidas"
        puntaje = 0
        circulosVerdes = 0
        calcularDificultadNivel()
        reiniciarCirculos()
        iniciarContador()
        mostrarProximoCirculoVerde()
    }

    private fun calcularDificultadNivel() {
        tiempoCirculoVerde = velocidadBase - (nivelActual * 100)
        circulosPorNivel = 5 + (nivelActual - 1) * 2
        puntosParaNivel = 5 + (nivelActual * 3)
    }

    private fun iniciarContador() {
        tiempoRestante = 30
        contadorTiempo = object : CountDownTimer(tiempoRestante * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = (millisUntilFinished / 1000).toInt()
                textoTiempo.text = "Tiempo: $tiempoRestante"
            }
            override fun onFinish() {
                perderVida()
            }
        }.start()
    }

    private fun reiniciarCirculos() {
        circulos.forEach {
            it.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
        }
    }

    private fun mostrarProximoCirculoVerde() {
        if (circulosVerdes < circulosPorNivel) {
            manejador.postDelayed({
                cambiarCirculoAleatorio()
                circulosVerdes++
                mostrarProximoCirculoVerde()
            }, tiempoCirculoVerde)
        } else {
            if (puntaje >= puntosParaNivel) {
                if (nivelActual == nivelMaximo) {
                    finalizarJuego(true)
                } else {
                    nivelActual++
                    reiniciarNivel()
                }
            } else {
                perderVida()
            }
        }
    }

    private fun cambiarCirculoAleatorio() {
        val circulosRojos = circulos.filter {
            it.background.constantState == ContextCompat.getDrawable(this,
                R.drawable.circle_red)?.constantState
        }
        if (circulosRojos.isNotEmpty()) {
            val circuloAleatorio = circulosRojos.random()
            circuloAleatorio.background =
                ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun alHacerClickEnCirculo(vista: View) {
        if (vista.background.constantState ==
            ContextCompat.getDrawable(this, R.drawable.circle_green)?.constantState) {
            vista.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    vista.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                }
            mediaPlayer.start()
            puntaje++
            vista.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
        } else {
            perderVida()
        }
    }

    private fun perderVida() {
        vidas--
        textoVidas.text = "Vidas: $vidas"
        if (vidas <= 0) {
            finalizarJuego(false)
        } else {
            reiniciarNivel()
        }
    }

    private fun reiniciarNivel() {
        contadorTiempo.cancel()
        manejador.removeCallbacksAndMessages(null)
        reiniciarCirculos()
        iniciarNivel()
    }

    private fun finalizarJuego(completado: Boolean) {
        contadorTiempo.cancel()
        manejador.removeCallbacksAndMessages(null)
        val intent = Intent(this, ResultadosActivity::class.java)
        intent.putExtra("PUNTAJE", puntaje)
        intent.putExtra("COMPLETADO", completado)
        intent.putExtra("NIVEL", nivelActual)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        contadorTiempo.cancel()
        manejador.removeCallbacksAndMessages(null)
    }
}
