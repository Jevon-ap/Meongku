package com.example.meongku.ui.main.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.meongku.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textProfile
//        UserViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

}