package com.cchavez.s03_usodesensores

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProximityActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var txtProximityValue: TextView
    private lateinit var btnGetValue: Button
    private lateinit var mainLayout: RelativeLayout
    private val initialBackgroundColor = Color.WHITE
    private val initialTextColor = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity)

        mainLayout = findViewById(R.id.proximityLayout)
        txtProximityValue = findViewById(R.id.txtProximityValue)

        // Initialize sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Toast.makeText(this, "El dispositivo no tiene sensor de proximidad", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val value = event.values[0]
        txtProximityValue.text = "Valor: $value"

        if (value < proximitySensor?.maximumRange ?: 0f) {
            // Object is near
            mainLayout.setBackgroundColor(Color.BLUE)
            txtProximityValue.setBackgroundColor(Color.RED)
            txtProximityValue.setTextColor(Color.WHITE)
        } else {
            // Object is far
            mainLayout.setBackgroundColor(initialBackgroundColor)
            txtProximityValue.setBackgroundColor(initialBackgroundColor)
            txtProximityValue.setTextColor(initialTextColor)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}