package com.mcmouse88.acivitydependency.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcmouse88.acivitydependency.R
import com.mcmouse88.acivitydependency.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProfileBinding.bind(view)

        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
        }

        viewModel.accountLiveData.observe(viewLifecycleOwner) {
            binding.tvHello.text = getString(R.string.hello, it.displayName)
        }
    }
}