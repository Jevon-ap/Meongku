package com.example.meongku.ui.main.cat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.catlist.Cat
import com.example.meongku.api.catlist.CatIdResponse
import com.example.meongku.api.catlist.CatResponse
import com.example.meongku.preference.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var catAdapter: CatListAdapter
    private lateinit var retrofitClient: RetrofitClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cat_list, container, false)
        recyclerView = view.findViewById(R.id.catRecyclerView)

        retrofitClient = RetrofitClient(UserPreferences(requireContext()))

        // Fetch the cat data using Retrofit
        retrofitClient.apiInstance().getAllCats().enqueue(object : Callback<CatResponse> {
            override fun onResponse(call: Call<CatResponse>, response: Response<CatResponse>) {
                if (response.isSuccessful) {
                    val catResponse = response.body()
                    val cats = catResponse?.cats ?: emptyList()
                    catAdapter.updateCats(cats)
                } else {
                    // Handle error case
                }
            }

            override fun onFailure(call: Call<CatResponse>, t: Throwable) {
                Log.d("CATLIST", "${t.message}")
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Create an instance of the CatListAdapter and set it to the RecyclerView
        catAdapter = CatListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = catAdapter

        // Set the click listener on the CatListAdapter
        catAdapter.setOnItemClickListener { catId ->
            // Call the API to get cat details by ID
            retrofitClient.apiInstance().getCatById(catId).enqueue(object : Callback<CatIdResponse> {
                override fun onResponse(call: Call<CatIdResponse>, response: Response<CatIdResponse>) {
                    if (response.isSuccessful) {
                        val catResponse = response.body()
                        val cat = catResponse?.cat
                        showCatDetails(cat)
                    } else {
                        // Handle error case
                    }
                }

                override fun onFailure(call: Call<CatIdResponse>, t: Throwable) {
                    Log.d("CATDETAILS", "${t.message}")
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    private fun showCatDetails(cat: Cat?) {
        // Implement your logic to show cat details (e.g., navigate to a new fragment/activity)
    }
}
