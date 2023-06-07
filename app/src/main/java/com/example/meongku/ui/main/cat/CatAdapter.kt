package com.example.meongku.ui.main.cat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meongku.R
import com.example.meongku.api.catlist.Cat

class CatAdapter(private val cats: List<Cat>) : RecyclerView.Adapter<CatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = cats[position]
        holder.bind(cat)
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val catImage: ImageView = itemView.findViewById(R.id.image)
        private val catTitle: TextView = itemView.findViewById(R.id.text_title)

        fun bind(cat: Cat) {
            // Load the cat image using Glide or any other image loading library
            Glide.with(itemView.context)
                .load(cat.photo)
                .into(catImage)

            catTitle.text = cat.race
        }
    }
}