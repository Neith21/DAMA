package com.cchavez.uso_de_libreria

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgView: ImageView = findViewById(R.id.img01)
        val btnUno: Button = findViewById(R.id.btnUno)
        val btnDos: Button = findViewById(R.id.btnDos)
        val btnTres: Button = findViewById(R.id.btnTres)
        val btnCuatro: Button = findViewById(R.id.btnCuatro)

        btnUno.setOnClickListener {
            imgView.setImageResource(R.drawable.imagen1)
        }

        btnDos.setOnClickListener {
            imgView.setImageResource(R.drawable.imagen2)
        }

        btnTres.setOnClickListener {
            imgView.setImageResource(R.drawable.imagen3)
        }

        btnCuatro.setOnClickListener {
            Picasso.get()
                .load("https://web-design-eastbourne.co.uk/News/wp-content/uploads/2023/11/Backend-Developer.jpeg")
                .into(imgView)
        }
    }
}
