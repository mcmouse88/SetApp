package com.mcmouse88.file_to_server.presentation.sign_in

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcmouse88.file_to_server.R
import com.mcmouse88.file_to_server.databinding.FragmentSignInBinding
import com.mcmouse88.file_to_server.presentation.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigInFragment : Fragment(R.layout.fragment_sign_in) {

    private val viewModel by viewModels<SignInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignInBinding.bind(view)

        binding.btnSignIn.setOnClickListener { viewModel.signIn() }
        binding.btnTryAgain.setOnClickListener { viewModel.load() }

        viewModel.navigateToProfileEvent.observeEvent(viewLifecycleOwner, ::launchProfileScreen)
        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            binding.renderState(it)
        }
    }

    private fun launchProfileScreen() {
        findNavController().navigate(R.id.action_files)
    }

    private fun FragmentSignInBinding.renderState(state: SignInViewModel.State) {
        btnSignIn.isVisible = state is SignInViewModel.State.NotLoggedIn
        progressBar.isVisible = state is SignInViewModel.State.Loading
        errorContainer.isVisible = state is SignInViewModel.State.Error
        if (state is SignInViewModel.State.Error) {
            tvErrorMessage.text = state.message
        }
    }
}