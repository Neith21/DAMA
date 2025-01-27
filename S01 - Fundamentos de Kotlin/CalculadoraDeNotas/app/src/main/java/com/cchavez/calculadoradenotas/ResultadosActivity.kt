package com.cchavez.calculadoradenotas

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultadosActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)

        // Obtener los extras del Intent
        val nombreEstudiante = intent.getStringExtra("nombre") ?: ""
        val notaMinima = intent.getDoubleExtra("notaMinima", 6.0)
        val promedioComputo1 = intent.getDoubleExtra("promedioComputo1", 0.0)
        val promedioComputo2 = intent.getDoubleExtra("promedioComputo2", 0.0)
        val promedioComputo3 = intent.getDoubleExtra("promedioComputo3", 0.0)

        // Obtener referencias a las vistas
        val nombreEstudianteText = findViewById<TextView>(R.id.nombreEstudianteText)
        val promedioComputo1Text = findViewById<TextView>(R.id.promedioComputo1Text)
        val promedioComputo2Text = findViewById<TextView>(R.id.promedioComputo2Text)
        val promedioComputo3Text = findViewById<TextView>(R.id.promedioComputo3Text)
        val promedioFinalText = findViewById<TextView>(R.id.promedioFinalText)
        val estadoFinalText = findViewById<TextView>(R.id.estadoFinalText)
        val notaFaltanteText = findViewById<TextView>(R.id.notaFaltanteText)
        val salirButton = findViewById<Button>(R.id.salirButton)

        // Mostrar nombre del estudiante
        nombreEstudianteText.text = "Estudiante: $nombreEstudiante"

        // Mostrar promedios de cada cómputo
        promedioComputo1Text.text = "Cómputo 1: %.2f".format(promedioComputo1)
        promedioComputo2Text.text = "Cómputo 2: %.2f".format(promedioComputo2)
        promedioComputo3Text.text = "Cómputo 3: %.2f".format(promedioComputo3)

        // Calcular promedio final
        val promedioFinal = (promedioComputo1 + promedioComputo2 + promedioComputo3) / 3.0
        promedioFinalText.text = "Promedio Final: %.2f".format(promedioFinal)

        // Determinar si aprobó y mostrar mensaje correspondiente
        if (promedioFinal >= notaMinima) {
            estadoFinalText.text = "¡APROBADO!"
            estadoFinalText.setTextColor(Color.GREEN)
            notaFaltanteText.visibility = View.GONE
        } else {
            estadoFinalText.text = "NO APROBADO"
            estadoFinalText.setTextColor(Color.RED)

            // Calcular cuánto le falta para aprobar
            val notaFaltante = notaMinima - promedioFinal
            notaFaltanteText.text = "Te faltaron %.2f puntos para aprobar".format(notaFaltante)
            notaFaltanteText.visibility = View.VISIBLE
        }

        // Configurar botón de salir
        salirButton.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }
    }
}