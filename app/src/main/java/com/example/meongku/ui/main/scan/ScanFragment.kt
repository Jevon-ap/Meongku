package com.example.meongku.ui.main.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.meongku.R
import com.example.meongku.databinding.FragmentScanBinding

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.captureImage.setOnClickListener {
            showResultScanFragment()
        }

        return root
    }

    private fun showResultScanFragment() {
        val resultScanFragment = ResultScanFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, resultScanFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}