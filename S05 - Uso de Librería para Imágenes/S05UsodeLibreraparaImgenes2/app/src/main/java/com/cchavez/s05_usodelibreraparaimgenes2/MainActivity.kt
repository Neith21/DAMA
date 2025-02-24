package com.cchavez.s05_usodelibreraparaimgenes2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var dotsLayout: LinearLayout
    private lateinit var handler: Handler
    private var currentImagePosition = 0
    private var dots: ArrayList<ImageView> = ArrayList()

    // Lista de imágenes
    private val images = arrayOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3,
        R.drawable.img4,
        R.drawable.img6,
        R.drawable.img3,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupDots()
        startImageSlider()
        setupMovieInfo()
    }

    private fun setupViews() {
        imageView = findViewById(R.id.imageView)
        dotsLayout = findViewById(R.id.dotsLayout)
        handler = Handler(Looper.getMainLooper())

        // Configurar el botón web
        findViewById<Button>(R.id.webButton).setOnClickListener {
            val url = "https://www.ghibli.jp/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun setupDots() {
        dots.clear()
        dotsLayout.removeAllViews()

        for (i in images.indices) {
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params

            if (i == currentImagePosition) {
                dot.setBackgroundResource(R.drawable.indicator_selector)
            } else {
                dot.setBackgroundResource(R.drawable.indicator_selector_inactive)
            }

            dots.add(dot)
            dotsLayout.addView(dot)
        }
    }

    private fun updateDots() {
        for (i in dots.indices) {
            if (i == currentImagePosition) {
                dots[i].setBackgroundResource(R.drawable.indicator_selector)
            } else {
                dots[i].setBackgroundResource(R.drawable.indicator_selector_inactive)
            }
        }
    }

    private fun startImageSlider() {
        handler.post(object : Runnable {
            override fun run() {
                imageView.setImageResource(images[currentImagePosition])
                updateDots()

                currentImagePosition++
                if (currentImagePosition >= images.size) {
                    currentImagePosition = 0
                }

                handler.postDelayed(this, 3000) // Cambiar imagen cada 3 segundos
            }
        })
    }

    private fun setupMovieInfo() {
        findViewById<TextView>(R.id.titleContent).text = "La Tumba de las Luciernagas"
        findViewById<TextView>(R.id.releaseDateContent).text = "16 de abril de 1988"
        findViewById<TextView>(R.id.authorsContent).text = "Isao Takahata\nAkiyuki Nosaka\nIsao Takahata"
        findViewById<TextView>(R.id.descriptionContent).text = "Un adolescente se ve obligado a cuidar a su hermana menor después de que un bombardeo aliado durante la Segunda Guerra Mundial destruye su casa."
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}