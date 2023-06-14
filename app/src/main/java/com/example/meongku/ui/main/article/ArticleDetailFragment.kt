package com.example.meongku.ui.main.article

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.article.Article
import com.example.meongku.api.article.ArticleIdResponse
import com.example.meongku.api.article.CreatedAt
import com.example.meongku.preference.UserPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticleDetailFragment : Fragment() {
    private lateinit var ivFotoArtikel: ImageView
    private lateinit var tvJudulArtikel: TextView
    private lateinit var tvKategoriArtikel: TextView
    private lateinit var tvTanggalArtikel: TextView
    private lateinit var tvDeskripsiArtikel: TextView
    private lateinit var retrofitClient: RetrofitClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_detail, container, false)

        ivFotoArtikel = view.findViewById(R.id.ivFotoArtikel)
        tvJudulArtikel = view.findViewById(R.id.tvJudulArtikel)
        tvKategoriArtikel = view.findViewById(R.id.tvKategoriArtikel)
        tvTanggalArtikel = view.findViewById(R.id.tvTanggalArtikel)
        tvDeskripsiArtikel = view.findViewById(R.id.tvDeskripsiArtikel)

        retrofitClient = RetrofitClient(UserPreferences(requireContext()))

        val articleId = arguments?.getString("articleId")

        Log.d("ARTICLEDETAILS", articleId.toString())

        if (!articleId.isNullOrEmpty()) {
            Log.d("ARTICLEDETAILS", "SUCCESS")

            retrofitClient.apiInstance().getArticleById(articleId).enqueue(object : Callback<ArticleIdResponse> {
                override fun onResponse(call: Call<ArticleIdResponse>, response: Response<ArticleIdResponse>) {
                    if (response.isSuccessful) {
                        val articleResponse = response.body()
                        val article = articleResponse?.article
                        article?.let { showArticleDetails(it) }
                    } else {
                        // Handle error case
                    }
                }

                override fun onFailure(call: Call<ArticleIdResponse>, t: Throwable) {
                    Log.d("ARTICLEDETAILS", "${t.message}")
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationBar()
    }

    private fun showArticleDetails(article: Article) {
        Glide.with(requireContext())
            .load(article.article_image)
            .into(ivFotoArtikel)

        tvJudulArtikel.text = article.article_title
        tvKategoriArtikel.text = article.article_category.joinToString(", ")
        tvTanggalArtikel.text = formatDate(article.created_at)
        tvDeskripsiArtikel.text = article.article_body
    }

    private fun hideBottomNavigationBar() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationBar() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationBar()
    }

    private fun formatDate(created_at: CreatedAt): String {
        val timestamp = created_at.seconds * 1000L + created_at.nanoseconds / 1000000L
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
