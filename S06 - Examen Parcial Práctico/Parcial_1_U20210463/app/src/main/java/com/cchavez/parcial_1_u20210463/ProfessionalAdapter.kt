package com.cchavez.parcial_1_u20210463

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfessionalAdapter(private val professionals: List<Professional>) :
    RecyclerView.Adapter<ProfessionalAdapter.ProfessionalViewHolder>() {

    class ProfessionalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageProfessional)
        val nameText: TextView = itemView.findViewById(R.id.textName)
        val professionText: TextView = itemView.findViewById(R.id.textProfession)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_professional, parent, false)
        return ProfessionalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfessionalViewHolder, position: Int) {
        val professional = professionals[position]

        holder.imageView.setImageResource(professional.imageResId)
        holder.nameText.text = professional.name
        holder.professionText.text = professional.profession

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("PROFESSIONAL_ID", professional.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = professionals.size
}
