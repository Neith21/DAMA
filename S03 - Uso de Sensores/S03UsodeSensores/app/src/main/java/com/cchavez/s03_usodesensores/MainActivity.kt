package com.cchavez.s03_usodesensores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnProximity: Button
    private lateinit var btnLight: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnProximity = findViewById(R.id.btnProximity)
        btnLight = findViewById(R.id.btnLight)

        btnProximity.setOnClickListener {
            startActivity(Intent(this, ProximityActivity::class.java))
        }

        btnLight.setOnClickListener {
            startActivity(Intent(this, LightActivity::class.java))
        }
    }
}