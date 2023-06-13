package com.example.meongku.ui.main.cat

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
import com.example.meongku.api.catlist.Cat
import com.example.meongku.api.catlist.CatIdResponse
import com.example.meongku.preference.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class CatDetailFragment : Fragment() {
    private lateinit var ivFotoKucing: ImageView
    private lateinit var tvRasKucing: TextView
    private lateinit var tvDeskripsiKucing: TextView
    private lateinit var retrofitClient: RetrofitClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cat_detail, container, false)

        ivFotoKucing = view.findViewById(R.id.ivFotoKucing)
        tvRasKucing = view.findViewById(R.id.tvRasKucing)
        tvDeskripsiKucing = view.findViewById(R.id.tvDeskripsiKucing)

        retrofitClient = RetrofitClient(UserPreferences(requireContext()))

        val catId = arguments?.getInt("catId")

        Log.d("CATDETAILS", catId.toString())

        if (catId != null) {
            Log.d("CATDETAILS", "BERHASIL")

            retrofitClient.apiInstance().getCatById(catId).enqueue(object : Callback<CatIdResponse> {
                override fun onResponse(call: Call<CatIdResponse>, response: Response<CatIdResponse>) {
                    if (response.isSuccessful) {
                        val catResponse = response.body()
                        val cat = catResponse?.cat
                        cat?.let { showCatDetails(it) }
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

    override fun onStop() {
        super.onStop()
        findNavController().navigateUp()
    }

    private fun showCatDetails(cat: Cat) {
        Glide.with(requireContext())
            .load(cat.photo)
            .into(ivFotoKucing)

        tvRasKucing.text = cat.race
        tvDeskripsiKucing.text = cat.desc
    }
}
