package com.cchavez.uso_camara

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var btnTomarFoto: Button
    private lateinit var imgFoto: ImageView
    private lateinit var currentPhotoPath: String


    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imgFoto.setImageURI(Uri.parse(currentPhotoPath))
            Toast.makeText(this,"Foto tomada y guardada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Error al tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            realizarProcesoFotografia()
        } else {
            Toast.makeText(this,"Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTomarFoto = findViewById(R.id.buttonTakePhoto)
        imgFoto = findViewById(R.id.imageView)

        btnTomarFoto.setOnClickListener {
            realizarProcesoFotografia()
        }
    }

    private fun realizarProcesoFotografia() {
        tomarFoto()
    }

    private fun tomarFoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                val photoFile = crearArchivo()
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "com.cchavez.uso_camara.fileprovider",
                    photoFile
                )

                currentPhotoPath = photoFile.absolutePath
                takePictureLauncher.launch(photoUri)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "Se necesita el permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun crearArchivo(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir("Pictures")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

}