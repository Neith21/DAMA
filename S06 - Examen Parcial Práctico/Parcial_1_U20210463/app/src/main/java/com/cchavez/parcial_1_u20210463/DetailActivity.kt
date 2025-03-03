package com.cchavez.parcial_1_u20210463

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val professionalId = intent.getIntExtra("PROFESSIONAL_ID", -1)
        val professional = MainActivity.professionals.find { it.id == professionalId }
            ?: return finish() // Finaliza la actividad si no se encuentra el profesional

        // Inicializar vistas
        val imageView: ImageView = findViewById(R.id.imageDetail)
        val nameText: TextView = findViewById(R.id.textDetailName)
        val professionText: TextView = findViewById(R.id.textDetailProfession)
        val aboutText: TextView = findViewById(R.id.textAbout)
        val emailText: TextView = findViewById(R.id.textEmail)
        val phoneText: TextView = findViewById(R.id.textPhone)
        val callButton: Button = findViewById(R.id.buttonCall)
        val whatsappButton: Button = findViewById(R.id.buttonWhatsapp)

        // Asignar datos
        imageView.setImageResource(professional.imageResId)
        nameText.text = professional.name
        professionText.text = professional.profession
        aboutText.text = professional.about
        emailText.text = professional.email
        phoneText.text = professional.phone

        // Configurar botones
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${professional.phone}")
            }
            startActivity(intent)
        }

        whatsappButton.setOnClickListener {
            try {
                // Formato para WhatsApp: https://wa.me/<número>
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${professional.phone}")
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback en caso de que WhatsApp no esté instalado
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=${professional.phone}")
                }
                startActivity(intent)
            }
        }
    }
}