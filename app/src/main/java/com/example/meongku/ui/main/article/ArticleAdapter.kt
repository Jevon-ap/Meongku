package com.example.meongku.ui.main.article

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meongku.R
import com.example.meongku.api.article.Article

class ArticleAdapter(private var articles: List<Article> = emptyList()) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val imageView: ImageView = itemView.findViewById(R.id.ivArtikel)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tvKategori)
        private val titleTextView: TextView = itemView.findViewById(R.id.tvJudul)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvTanggal)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(article: Article) {
            categoryTextView.text = article.articleCategory?.joinToString(", ") ?: ""
            titleTextView.text = article.articleTitle
            dateTextView.text = formatDate(article.createdAt.seconds)

            // Load article image using Glide or any other image loading library
            Glide.with(itemView.context)
                .load(article.articleImage)
                .into(imageView)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val article = articles[position]
                onItemClick?.invoke(article.id)
            }
        }
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClick = listener
    }

    private fun formatDate(seconds: Long): String {
        // Convert seconds to the desired date format
        // You can use SimpleDateFormat or any other date formatting approach here
        // Example:
        // val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // return dateFormat.format(Date(seconds * 1000))

        // Placeholder implementation, return seconds as string
        return seconds.toString()
    }
}
