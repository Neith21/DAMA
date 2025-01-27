package com.cchavez.calculadoradenotas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var notasComputo1 = mutableListOf<Double>()
    private var notasComputo2 = mutableListOf<Double>()
    private var notasComputo3 = mutableListOf<Double>()
    private var notaMinima = 0.0
    private var nombreEstudiante = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nombreEditText = findViewById<EditText>(R.id.nombreEstudiante)
        val notaMinimaEditText = findViewById<EditText>(R.id.notaMinima)
        val computoGroup = findViewById<RadioGroup>(R.id.computoGroup)
        val nota1EditText = findViewById<EditText>(R.id.nota1)
        val nota2EditText = findViewById<EditText>(R.id.nota2)
        val nota3EditText = findViewById<EditText>(R.id.nota3)
        val calcularButton = findViewById<Button>(R.id.calcularButton)
        val verResultadosButton = findViewById<Button>(R.id.verResultadosButton)

        calcularButton.setOnClickListener {
            if (validarCampos(nombreEditText, notaMinimaEditText, nota1EditText, nota2EditText, nota3EditText)) {
                // Guardar nombre y nota mínima
                nombreEstudiante = nombreEditText.text.toString()
                notaMinima = notaMinimaEditText.text.toString().toDouble()

                // Guardar notas según el cómputo seleccionado
                guardarNotasComputo(
                    computoGroup,
                    nota1EditText.text.toString().toDouble(),
                    nota2EditText.text.toString().toDouble(),
                    nota3EditText.text.toString().toDouble()
                )

                // Mostrar promedio del cómputo actual
                mostrarPromedioComputo(computoGroup.checkedRadioButtonId)

                // Limpiar campos de notas para el siguiente cómputo
                nota1EditText.text.clear()
                nota2EditText.text.clear()
                nota3EditText.text.clear()
            }
        }

        verResultadosButton.setOnClickListener {
            if (validarTodosLosComputos()) {
                val intent = Intent(this, ResultadosActivity::class.java).apply {
                    putExtra("nombre", nombreEstudiante)
                    putExtra("notaMinima", notaMinima)
                    putExtra("promedioComputo1", calcularPromedio(notasComputo1))
                    putExtra("promedioComputo2", calcularPromedio(notasComputo2))
                    putExtra("promedioComputo3", calcularPromedio(notasComputo3))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Debe completar todos los cómputos primero", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validarCampos(
        nombreEditText: EditText,
        notaMinimaEditText: EditText,
        nota1EditText: EditText,
        nota2EditText: EditText,
        nota3EditText: EditText
    ): Boolean {
        if (nombreEditText.text.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del estudiante", Toast.LENGTH_SHORT).show()
            return false
        }

        if (notaMinimaEditText.text.isEmpty()) {
            Toast.makeText(this, "Ingrese la nota mínima", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que todas las notas estén ingresadas
        if (nota1EditText.text.isEmpty() || nota2EditText.text.isEmpty() || nota3EditText.text.isEmpty()) {
            Toast.makeText(this, "Ingrese todas las notas", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que las notas estén entre 0 y 10
        val nota1 = nota1EditText.text.toString().toDoubleOrNull()
        val nota2 = nota2EditText.text.toString().toDoubleOrNull()
        val nota3 = nota3EditText.text.toString().toDoubleOrNull()

        if (nota1 == null || nota2 == null || nota3 == null ||
            nota1 > 10 || nota2 > 10 || nota3 > 10 ||
            nota1 < 0 || nota2 < 0 || nota3 < 0) {
            Toast.makeText(this, "Las notas deben estar entre 0 y 10", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun guardarNotasComputo(computoGroup: RadioGroup, nota1: Double, nota2: Double, nota3: Double) {
        val notas = listOf(nota1, nota2, nota3)

        when (computoGroup.checkedRadioButtonId) {
            R.id.computo1 -> {
                notasComputo1.clear()
                notasComputo1.addAll(notas)
            }
            R.id.computo2 -> {
                notasComputo2.clear()
                notasComputo2.addAll(notas)
            }
            R.id.computo3 -> {
                notasComputo3.clear()
                notasComputo3.addAll(notas)
            }
            else -> {
                Toast.makeText(this, "Seleccione un cómputo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calcularPromedio(notas: List<Double>): Double {
        return if (notas.isEmpty()) 0.0 else notas.average()
    }

    private fun mostrarPromedioComputo(computoId: Int) {
        val promedio = when (computoId) {
            R.id.computo1 -> calcularPromedio(notasComputo1)
            R.id.computo2 -> calcularPromedio(notasComputo2)
            R.id.computo3 -> calcularPromedio(notasComputo3)
            else -> 0.0
        }

        val mensaje = "Promedio del cómputo: %.2f".format(promedio)
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun validarTodosLosComputos(): Boolean {
        val todosCompletos = notasComputo1.isNotEmpty() &&
                notasComputo2.isNotEmpty() &&
                notasComputo3.isNotEmpty()

        if (!todosCompletos) {
            val mensaje = "Faltan cómputos por calcular"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        return todosCompletos
    }
}