package com.example.meongku.ui.main.cat

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.meongku.databinding.FragmentCatBinding


class CatFragment : Fragment() {
    private var _binding: FragmentCatBinding?= null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val catViewModel =
            ViewModelProvider(this).get(CatViewModel::class.java)

        _binding = FragmentCatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCat
        catViewModel.text.observe(viewLifecycleOwner) {
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