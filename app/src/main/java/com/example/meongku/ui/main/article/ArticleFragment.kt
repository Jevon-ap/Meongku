package com.example.meongku.ui.main.article

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.article.Article
import com.example.meongku.api.article.ArticleResponse
import com.example.meongku.preference.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var retrofitClient: RetrofitClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        recyclerView = view.findViewById(R.id.articleRecyclerView)

        retrofitClient = RetrofitClient(UserPreferences(requireContext()))

        retrofitClient.apiInstance().getAllArticles().enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    val articleResponse = response.body()
                    val articles = articleResponse?.articles ?: emptyList()
                    articleAdapter.updateArticles(articles)
                } else {
                    Log.d("ARTICLE LIST", "FAILED: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.d("ARTICLE LIST", "${t.message}")
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })

        articleAdapter = ArticleAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

        articleAdapter.setOnItemClickListener { articleId ->
            Log.d("ARTICLE LIST", articleId.toString())
            val action = ArticleFragmentDirections.actionArticleFragmentToArticleDetailFragment(articleId)
            findNavController().navigate(action)
        }

        return view
    }
}
