package com.cchavez.parcial_1_u20210463

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfessionalAdapter

    companion object {
        // Lista estática de profesionales para acceder desde DetailActivity
        val professionals = listOf(
            Professional(
                id = 1,
                name = "Dr. Ana García",
                profession = "Médico Cardióloga",
                about = "Especialista en cardiología con más de 10 años de experiencia. Graduada de la Universidad Nacional con múltiples especializaciones en el tratamiento de enfermedades cardiovasculares.",
                email = "ana.garcia@ejemplo.com",
                phone = "1234567890",
                imageResId = R.drawable.doctor
            ),
            Professional(
                id = 2,
                name = "Ing. Carlos Rodríguez",
                profession = "Ingeniero Civil",
                about = "Ingeniero Civil con especialidad en estructuras. Ha participado en grandes proyectos de infraestructura urbana y cuenta con certificaciones internacionales en gestión de proyectos.",
                email = "carlos.rodriguez@ejemplo.com",
                phone = "2345678901",
                imageResId = R.drawable.engineer
            ),
            Professional(
                id = 3,
                name = "Lic. Sofía López",
                profession = "Abogada Penalista",
                about = "Abogada especializada en derecho penal con más de 15 años de trayectoria. Reconocida por su dedicación y éxito en casos de alta complejidad.",
                email = "sofia.lopez@ejemplo.com",
                phone = "3456789012",
                imageResId = R.drawable.lawyer
            ),
            Professional(
                id = 4,
                name = "Prof. Miguel Torres",
                profession = "Arquitecto",
                about = "Arquitecto con enfoque en diseño sostenible. Ha ganado múltiples premios por sus innovadores proyectos que combinan estética, funcionalidad y respeto al medio ambiente.",
                email = "miguel.torres@ejemplo.com",
                phone = "4567890123",
                imageResId = R.drawable.architect
            ),
            Professional(
                id = 5,
                name = "Dra. Elena Martínez",
                profession = "Psicóloga Clínica",
                about = "Psicóloga especializada en terapia cognitivo-conductual con amplia experiencia en el tratamiento de trastornos de ansiedad y depresión. Docente universitaria y autora de publicaciones sobre salud mental.",
                email = "elena.martinez@ejemplo.com",
                phone = "5678901234",
                imageResId = R.drawable.psychologist
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerProfessionals)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProfessionalAdapter(professionals)
        recyclerView.adapter = adapter
    }
}