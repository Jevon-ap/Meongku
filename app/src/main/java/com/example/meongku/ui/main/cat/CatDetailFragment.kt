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
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.meongku.R
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.catlist.Cat
import com.example.meongku.api.catlist.CatIdResponse
import com.example.meongku.preference.UserPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    private fun showBottomNavigationBar() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationBar()
    }

    private fun hideBottomNavigationBar() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intercept the back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.navigation_cat, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        hideBottomNavigationBar()
    }


    private fun showCatDetails(cat: Cat) {
        Glide.with(requireContext())
            .load(cat.photo)
            .into(ivFotoKucing)

        tvRasKucing.text = cat.race
        tvDeskripsiKucing.text = cat.desc
    }

    override fun onStop() {
        super.onStop()

        // Navigate up to the access point of cat race list fragment
        findNavController().popBackStack(R.id.navigation_cat, false)
    }
}
