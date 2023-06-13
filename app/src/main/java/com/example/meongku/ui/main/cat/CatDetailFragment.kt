package com.example.meongku.ui.main.cat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.meongku.R
import com.example.meongku.api.catlist.Cat

class CatDetailFragment : Fragment() {
    private lateinit var ivFotoKucing: ImageView
    private lateinit var tvRasKucing: TextView
    private lateinit var tvDeskripsiKucing: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cat_detail, container, false)

        ivFotoKucing = view.findViewById(R.id.ivFotoKucing)
        tvRasKucing = view.findViewById(R.id.tvRasKucing)
        tvDeskripsiKucing = view.findViewById(R.id.tvDeskripsiKucing)

        // Retrieve cat data from arguments
        val cat = arguments?.getParcelable<Cat>(ARG_CAT)

        Log.d("CATDETAILS2", "BERHASIL ARGUMEN ${cat?.race}")

        cat?.let { showCatDetails(it) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cat = arguments?.getParcelable<Cat>(ARG_CAT)
        if (cat != null) {
            Log.d("CATDETAILS", "BERHASIL ${cat.race}")
        } else {
            Log.d("CATDETAILS", "GAGAL")
        }
    }


    private fun showCatDetails(cat: Cat) {
        // Load image using Glide
        Glide.with(requireContext())
            .load(cat.photo)
            .into(ivFotoKucing)

        Log.d("CATDETAILS", "MASUK KE DETAILS")

        // Set text values
        tvRasKucing.text = cat.race
        tvDeskripsiKucing.text = cat.desc
    }

    companion object {
        private const val ARG_CAT = "arg_cat"

        fun newInstance(cat: Cat): CatDetailFragment {
            val args = Bundle().apply {
                putParcelable(ARG_CAT, cat)
            }
            val fragment = CatDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
