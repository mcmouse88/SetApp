package com.mcmouse88.okhttp.presentation.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.databinding.FragmentSignInBinding
import com.mcmouse88.okhttp.presentation.base.BaseFragment
import com.mcmouse88.okhttp.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {

    override val viewModel by viewModels<SignInViewModel>()

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding ?: throw NullPointerException("FragmentSignInBinding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        binding.buttonSignIn.setOnClickListener { onSignInButtonPressed() }
        binding.buttonSignUp.setOnClickListener { onSignUpButtonPressed() }

        observeState()
        observeClearPasswordEvent()
        observeShowAuthErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onSignInButtonPressed() {
        viewModel.signIn(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString()
        )
    }

    private fun onSignUpButtonPressed() {
        val email = binding.etEmail.text.toString()
        val emailArg = email.ifBlank { null }

        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment(emailArg)
        findNavController().navigate(direction)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.apply {
            tilEmail.error = if (it.emptyEmailError) getString(R.string.field_is_empty) else null
            tilPassword.error = if (it.emptyPasswordError) getString(R.string.field_is_empty) else null

            tilEmail.isEnabled = it.enableViews
            tilPassword.isEnabled = it.enableViews
            buttonSignIn.isEnabled = it.enableViews
            buttonSignUp.isEnabled = it.enableViews
            progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun observeShowAuthErrorMessageEvent() = viewModel.showAuthErrorToastEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }

    private fun observeClearPasswordEvent() = viewModel.clearPasswordEvent.observeEvent(viewLifecycleOwner) {
        binding.etPassword.text?.clear()
    }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }
}