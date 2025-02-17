package com.cchavez.tabs

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class QuienSoyFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quien_soy,
            container, false)
        view.findViewById<TextView>(R.id.tvNombre).text = "Nombre: Cristian Ariel"
        view.findViewById<TextView>(R.id.tvApellido).text = "Apellido: Chávez Torres"
        view.findViewById<TextView>(R.id.tvCarnet).text = "Carnet: U20210463"
        view.findViewById<TextView>(R.id.tvTelefono).text = "Teléfono: +503 7239 2851"
        view.findViewById<Button>(R.id.btnEscribeme).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://wa.me/50372392851")
            startActivity(intent)
        }
        return view
    }
}