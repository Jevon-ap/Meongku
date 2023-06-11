package com.example.meongku.ui.main.food

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.meongku.databinding.FragmentFoodBinding


class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val foodViewModel =
            ViewModelProvider(this).get(FoodViewModel::class.java)

        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        foodViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
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