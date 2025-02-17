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

class TecnologiasFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tecnologias,
            container, false)
        val tvTecnologias =
            view.findViewById<TextView>(R.id.tvTecnologias)
        tvTecnologias.text = "- C#\n- Python \n-Rust \n- Java\n- HTML \n- CSS \n- JavaScript \n- Kotlin \n- Sql y NoSql \nAzure y AWS"

        view.findViewById<Button>(R.id.btnContactame).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL,
                    arrayOf("cristian.1945theend@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Me interesa tus servicios")
            }
            startActivity(intent)
        }
        return view
    }
}