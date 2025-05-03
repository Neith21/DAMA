package com.cchavez.s13_proyectoandroid_php_mysql_parte3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cchavez.s13_proyectoandroid_php_mysql_parte3.clases.Configuraciones
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var recyclerViewContactos: RecyclerView
    private lateinit var adapter: ContactoAdapter
    private val contactos = mutableListOf<Contacto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el Toolbar como ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        editTextNombre = findViewById(R.id.editTextNombre)
        recyclerViewContactos = findViewById(R.id.recyclerViewContactos)
        val buttonBuscar = findViewById<Button>(R.id.buttonBuscar)
        val buttonAgregar = findViewById<Button>(R.id.buttonAgregar)
        adapter = ContactoAdapter(contactos)
        recyclerViewContactos.layoutManager = LinearLayoutManager(this)
        recyclerViewContactos.adapter = adapter

        buttonBuscar.setOnClickListener {
            val filtro = editTextNombre.text.toString()
            if (filtro.isNotEmpty()) {
                buscarContactos(filtro)
            } else {
                cargarContactosIniciales()
            }
        }

        // Botón AGREGAR
        buttonAgregar.setOnClickListener {
            val intent = Intent(this, AgregarContactoActivity::class.java)
            startActivityForResult(intent, 1)
        }

        cargarContactosIniciales()
    }

    // Método para cargar todos los contactos al inicio
    private fun cargarContactosIniciales() {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.URL_WEBSERVICES}?action=listarContactos"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")
                    if (status == "success") {
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            val jsonArray = jsonObject.getJSONArray("data")
                            contactos.clear()
                            for (i in 0 until jsonArray.length()) {
                                val contacto = jsonArray.getJSONObject(i)
                                val id =
                                    if (contacto.has("id_contacto") && !contacto.isNull("id_contacto")) contacto.getInt(
                                        "id_contacto"
                                    ) else 0
                                val nombre =
                                    if (contacto.has("nombre") && !contacto.isNull("nombre")) contacto.getString(
                                        "nombre"
                                    ) else "Sin nombre"
                                val telefono =
                                    if (contacto.has("telefono") && !contacto.isNull("telefono")) contacto.getString(
                                        "telefono"
                                    ) else "Sin teléfono"
                                contactos.add(Contacto(id, nombre, telefono))
                            }
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this, "No se encontraron contactos", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        val message =
                            jsonObject.optString("message", "Error desconocido")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error al parsear respuesta: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error de conexión: ${error.message ?: "Desconocido"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        queue.add(stringRequest)
    }

    // Método para buscar contactos con filtro usando ListarContactosConFiltro
    private fun buscarContactos(filtro: String) {
        val queue = Volley.newRequestQueue(this)
        val url =
            "${Configuraciones.URL_WEBSERVICES}?action=listarContactosConFiltro&filtro=$filtro"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")

                    if (status == "success") {
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            val jsonArray = jsonObject.getJSONArray("data")
                            contactos.clear()
                            for (i in 0 until jsonArray.length()) {
                                val contacto = jsonArray.getJSONObject(i)
                                val id =
                                    if (contacto.has("id_contacto") && !contacto.isNull("id_contacto")) contacto.getInt(
                                        "id_contacto"
                                    ) else 0
                                val nombre =
                                    if (contacto.has("nombre") && !contacto.isNull("nombre")) contacto.getString(
                                        "nombre"
                                    ) else "Sin nombre"
                                val telefono =
                                    if (contacto.has("telefono") && !contacto.isNull("telefono")) contacto.getString(
                                        "telefono"
                                    ) else "Sin teléfono"
                                contactos.add(Contacto(id, nombre, telefono))
                            }
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this, "No se encontraron contactos", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        val message = jsonObject.optString("message", "Error desconocido")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error al parsear respuesta: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error de conexión: ${error.message ?: "Desconocido"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        queue.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarContactosIniciales()
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            cargarContactosIniciales()
        }
    }
}

data class Contacto(val id: Int, val nombre: String, val telefono: String)

class ContactoAdapter(private val contactos: List<Contacto>) :
    RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    class ContactoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContacto: TextView = itemView.findViewById(R.id.textViewContacto)
        val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        val buttonVerContacto: Button = itemView.findViewById(R.id.buttonVerContacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto, parent, false)
        return ContactoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.textViewContacto.text = contacto.nombre
        holder.textViewTelefono.text = contacto.telefono
        holder.buttonVerContacto.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ModificarContactoActivity::class.java).apply {
                putExtra("id_contacto", contacto.id)
                putExtra("nombre", contacto.nombre)
                putExtra("telefono", contacto.telefono)
            }
            (context as AppCompatActivity).startActivityForResult(intent, 2)
        }
    }

    override fun getItemCount(): Int = contactos.size
}
