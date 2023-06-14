package com.example.meongku.ui.main.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.article.Article
import com.example.meongku.api.article.ArticleResponse
import com.example.meongku.api.catlist.CatResponse
import com.example.meongku.databinding.FragmentHomeBinding
import com.example.meongku.preference.UserPreferences
import com.example.meongku.ui.main.cat.CatListAdapter
import com.example.meongku.ui.main.article.ArticleAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView

    private lateinit var catAdapter: CatListAdapter
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var retrofitClient: RetrofitClient

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView1 = binding.rvRasKucing
        recyclerView2 = binding.rvArtikelKucing

        recyclerView1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())

        retrofitClient = RetrofitClient(UserPreferences(requireContext()))

        catAdapter = CatListAdapter() // Instantiate the CatListAdapter
        articleAdapter = ArticleAdapter()

        recyclerView1.adapter = catAdapter
        recyclerView2.adapter = articleAdapter

        retrofitClient.apiInstance().getAllCats()
            .enqueue(object : Callback<CatResponse> {
                override fun onResponse(
                    call: Call<CatResponse>,
                    response: Response<CatResponse>
                ) {
                    if (response.isSuccessful) {
                        val catResponse = response.body()
                        val cats = catResponse?.cats?.take(4) ?: emptyList()
                        catAdapter.updateCats(cats)
                    } else {
                        Log.d("CAT LIST", "GAGAL: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CatResponse>, t: Throwable) {
                    Log.d("CATLIST", "${t.message}")
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })

        retrofitClient.apiInstance().getAllArticles().enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    val articleResponse = response.body()
                    val articles = articleResponse?.articles?.take(4) ?: emptyList()
                    Log.d("ARTICLE LIST", "First article: $articleResponse")
                    articleAdapter.updateArticles(articles)
                    Log.d("ARTICLE LIST", "Articles fetched successfully: ${articles.size} articles")
                    val firstArticle = articles[0]
                    Log.d("ARTICLE LIST", "First article: $firstArticle")

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

        val tvLinkRas: TextView = binding.tvLinkRas
        tvLinkRas.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.navigation_cat)
        }

        val tvLinkArticle: TextView = binding.tvLinkArtikel
        tvLinkArticle.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.articleFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        showSystemUI()
    }

    private fun showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}

