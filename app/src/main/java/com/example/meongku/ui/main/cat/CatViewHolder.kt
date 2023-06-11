package com.example.meongku.ui.main.cat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meongku.R

// CatViewHolder.kt
class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val catImage = itemView.findViewById<ImageView>(R.id.cat_image)
    private val catName = itemView.findViewById<TextView>(R.id.cat_name)

    fun bind(catItem: CatItem) {
        // Load the image from the URL using Glide or Picasso
        Glide.with(itemView.context)
            .load(catItem.imageUrl)
            .into(catImage)

        // Set the text from the race field
        catName.text = catItem.race
    }
}
