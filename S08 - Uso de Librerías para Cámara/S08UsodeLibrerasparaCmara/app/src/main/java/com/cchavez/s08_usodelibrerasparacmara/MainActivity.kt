package com.cchavez.s08_usodelibrerasparacmara

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.os.Build
import com.cchavez.s08_usodelibrerasparacmara.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentPhotoPath: String
    private var photoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            setPic()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        ) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(
                this,
                "Se necesitan permisos para usar la cámara",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTomarFoto.setOnClickListener {
            checkPermissionsAndTakePhoto()
        }

        binding.imgCorreo.setOnClickListener {
            photoUri?.let { uri ->
                shareByEmail(uri)
            } ?: Toast.makeText(
                this,
                "Primero debes tomar una foto",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.imgWhatsApp.setOnClickListener {
            photoUri?.let { uri ->
                shareByWhatsApp(uri)
            } ?: Toast.makeText(
                this,
                "Primero debes tomar una foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermissionsAndTakePhoto() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        when {
            permissionsToRequest.isEmpty() -> {
                dispatchTakePictureIntent()
            }
            else -> {
                // Solicitar permisos
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Crear el archivo donde debería ir la foto
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error al crear el archivo
                Toast.makeText(
                    this,
                    "Error al crear el archivo de imagen",
                    Toast.LENGTH_SHORT
                ).show()
                null
            }

            photoFile?.also {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureLauncher.launch(takePictureIntent)
            }
        } else {
            Toast.makeText(
                this,
                "No se encontró una aplicación de cámara",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun setPic() {
        val targetW: Int = binding.imgFoto.width
        val targetH: Int = binding.imgFoto.height

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            binding.imgFoto.setImageBitmap(bitmap)
        }
    }

    private fun shareByEmail(imageUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_SUBJECT, "Foto compartida desde mi aplicación")
            putExtra(Intent.EXTRA_TEXT, "Te comparto esta foto tomada con mi aplicación de Gestión de Fotos.")
        }

        if (intent.resolveActivity(packageManager) != null) {
            val chooser = Intent.createChooser(intent, "Compartir por correo")
            startActivity(chooser)
        } else {
            Toast.makeText(
                this,
                "No se encontró una aplicación para compartir por correo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareByWhatsApp(imageUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "Te comparto esta foto tomada con mi aplicación de Gestión de Fotos.")
            // Especificar WhatsApp como destino
            `package` = "com.whatsapp"
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "WhatsApp no está instalado en este dispositivo",
                Toast.LENGTH_SHORT
            ).show()

            val intentAlternativo = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, imageUri)
            }
            val chooser = Intent.createChooser(intentAlternativo, "Compartir imagen")
            startActivity(chooser)
        }
    }
}