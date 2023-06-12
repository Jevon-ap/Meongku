package com.example.meongku.ui.main.cat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meongku.R
import com.example.meongku.api.catlist.Cat

class CatListAdapter(private var cats: List<Cat> = emptyList()) :
    RecyclerView.Adapter<CatListAdapter.CatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cat_list, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = cats[position]
        holder.bind(cat)
    }

    override fun getItemCount(): Int = cats.size

    fun updateCats(newCats: List<Cat>) {
        cats = newCats
        notifyDataSetChanged()
    }

    inner class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivKucing)
        private val textView: TextView = itemView.findViewById(R.id.tvRas)

        fun bind(cat: Cat) {
            textView.text = cat.race
            imageView.setImageResource(cat.photo)
        }
    }
}
