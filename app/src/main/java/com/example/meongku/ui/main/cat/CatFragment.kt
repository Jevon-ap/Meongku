package com.example.meongku.ui.main.cat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
}