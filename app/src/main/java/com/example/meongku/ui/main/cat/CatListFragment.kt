package com.example.meongku.ui.main.cat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
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

        Log.d("SINI BANGGG", "BERHASIL SAMPAI SINI") // Log a debug message

        // Fetch the cat data using Retrofit

        retrofitClient.apiInstance().getAllCats().enqueue(object : Callback<CatResponse> {
            override fun onResponse(call: Call<CatResponse>, response: Response<CatResponse>) {
                if (response.isSuccessful) {
                    val catResponse = response.body()
                    val cats = catResponse?.cats ?: emptyList()
                    Log.d("SINI BANGGG", "BERHASIL SAMPAI SINI") // Log a debug message
                    catAdapter.updateCats(cats)
                } else {
                    Log.d("CAT LIST", "GAGAL: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "ERORR", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CatResponse>, t: Throwable) {
                Log.d("CATLIST", "${t.message}")
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Create an instance of the CatListAdapter and set it to the RecyclerView
        catAdapter = CatListAdapter()

        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = catAdapter

        // Set the click listener on the CatListAdapter
        catAdapter.setOnItemClickListener { catId ->
            Log.d("CATLIST", catId.toString())
            val action = CatListFragmentDirections.actionCatListFragmentToCatDetailFragment(catId)
            findNavController(this).navigate(action)
        }

        return view
    }
}
