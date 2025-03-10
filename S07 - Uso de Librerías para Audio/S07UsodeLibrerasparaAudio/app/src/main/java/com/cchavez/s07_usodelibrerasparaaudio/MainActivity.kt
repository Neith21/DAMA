package com.cchavez.s07_usodelibrerasparaaudio

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var isSpanish = true
    private var mediaPlayer: MediaPlayer? = null

    // Lista de colores en español e inglés
    private val colorList = listOf(
        ColorItem(R.drawable.rojo, "Rojo", "Red", R.raw.audio_rojo, R.raw.audio_red),
        ColorItem(R.drawable.azul, "Azul", "Blue", R.raw.audio_azul, R.raw.audio_blue),
        ColorItem(R.drawable.verde, "Verde", "Green", R.raw.audio_verde, R.raw.audio_green),
        ColorItem(R.drawable.amarillo, "Amarillo", "Yellow", R.raw.audio_amarillo, R.raw.audio_yellow),
        ColorItem(R.drawable.negro, "Negro", "Black", R.raw.audio_negro, R.raw.audio_black),
        ColorItem(R.drawable.blanco, "Blanco", "White", R.raw.audio_blanco, R.raw.audio_white),
        ColorItem(R.drawable.naranja, "Naranja", "Orange", R.raw.audio_naranja, R.raw.audio_orange),
        ColorItem(R.drawable.morado, "Morado", "Purple", R.raw.audio_morado, R.raw.audio_purple),
        ColorItem(R.drawable.rosa, "Rosa", "Pink", R.raw.audio_rosa, R.raw.audio_pink),
        ColorItem(R.drawable.marron, "Marrón", "Brown", R.raw.audio_marron, R.raw.audio_brown)
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout = findViewById<GridLayout>(R.id.colorGrid)
        val languageSwitch = findViewById<CardView>(R.id.languageSwitch)
        val languageText = findViewById<TextView>(R.id.languageText)

        // Configurar el interruptor de idioma
        languageSwitch.setOnClickListener {
            isSpanish = !isSpanish
            updateLanguageText(languageText)
            updateColorGrid(gridLayout)
        }

        // Configuración inicial
        updateLanguageText(languageText)
        createColorGrid(gridLayout)
    }

    private fun updateLanguageText(textView: TextView) {
        textView.text = if (isSpanish) "Español" else "English"
    }

    private fun createColorGrid(gridLayout: GridLayout) {
        gridLayout.removeAllViews()

        for (i in colorList.indices) {
            val colorItem = colorList[i]
            val cardView = layoutInflater.inflate(R.layout.color_item, gridLayout, false) as CardView

            val colorImage = cardView.findViewById<ImageView>(R.id.colorImage)
            val colorText = cardView.findViewById<TextView>(R.id.colorText)

            colorImage.setImageResource(colorItem.imageResId)
            colorText.text = if (isSpanish) colorItem.spanishName else colorItem.englishName

            cardView.setOnClickListener {
                playColorSound(colorItem)
            }

            // Configurar los parámetros de layout para asegurar que se muestre correctamente
            val param = GridLayout.LayoutParams()
            param.width = 0
            param.height = GridLayout.LayoutParams.WRAP_CONTENT
            param.columnSpec = GridLayout.spec(i % 2, 1, 1f)
            param.rowSpec = GridLayout.spec(i / 2, 1)
            param.setMargins(8, 8, 8, 8)

            cardView.layoutParams = param
            gridLayout.addView(cardView)
        }
    }

    private fun updateColorGrid(gridLayout: GridLayout) {
        for (i in 0 until gridLayout.childCount) {
            val cardView = gridLayout.getChildAt(i) as CardView
            val colorText = cardView.findViewById<TextView>(R.id.colorText)
            val colorItem = colorList[i]

            colorText.text = if (isSpanish) colorItem.spanishName else colorItem.englishName
        }
    }

    private fun playColorSound(colorItem: ColorItem) {
        // Liberar recursos del MediaPlayer anterior si existe
        mediaPlayer?.release()

        // Crear un nuevo MediaPlayer con el audio correspondiente
        val soundResId = if (isSpanish) colorItem.spanishSoundResId else colorItem.englishSoundResId
        mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer?.setOnCompletionListener { it.release() }
        mediaPlayer?.start()

        // Mostrar un mensaje
        Toast.makeText(
            this,
            if (isSpanish) "¡${colorItem.spanishName}!" else "${colorItem.englishName}!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}