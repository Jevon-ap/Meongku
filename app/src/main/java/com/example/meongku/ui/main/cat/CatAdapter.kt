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

// CatAdapter.kt
class CatAdapter : RecyclerView.Adapter<CatViewHolder>() {

    private val catList = mutableListOf<CatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        // Inflate the cat item layout and return a new view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        // Bind the data from the list to the view holder
        val catItem = catList[position]
        holder.bind(catItem)
    }

    override fun getItemCount(): Int {
        // Return the size of the list
        return catList.size
    }

    fun updateData(newCatList: List<CatItem>) {
        // Clear and update the list with new data
        catList.clear()
        catList.addAll(newCatList)
        notifyDataSetChanged()
    }
}
