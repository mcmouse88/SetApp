package com.mcmouse88.open_box.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcmouse88.open_box.R
import com.mcmouse88.open_box.databinding.FragmentSecretBinding

class SecretFragment : Fragment(R.layout.fragment_secret) {

    private var _binding: FragmentSecretBinding? = null
    private val binding: FragmentSecretBinding
        get() = _binding ?: throw NullPointerException("FragmentSecretBinding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSecretBinding.bind(view)

        binding.buttonCloseBox.setOnClickListener {
            findNavController().popBackStack(R.id.rootFragment, false)
        }

        binding.buttonGoBackFromSecret.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}