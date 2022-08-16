package com.mcmouse.nav_tabs.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentSignUpBinding
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding ?: throw NullPointerException("FragmentSignUpBinding is null")

    private val viewModel by viewModelCreator { SignUpViewModel(Repositories.accountsRepository) }

    private val args by navArgs<SignUpFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)
        binding.buttonCreateAccount.setOnClickListener { ovCreateAccountButtonPressed() }

        if (savedInstanceState == null && getEmailArguments() != null) {
            binding.etEmail.setText(getEmailArguments())
        }

        observeState()
        observeGoBackEven()
        observeShowSuccessSignUpMessageEvent()
    }

    private fun ovCreateAccountButtonPressed() {
        val signUpData = SignUpData(
            email = binding.etEmail.text.toString(),
            username = binding.etUsername.text.toString(),
            password = binding.etPassword.text.toString(),
            repeatPassword = binding.etRepeatPassword.text.toString()
        )
        viewModel.signUp(signUpData)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        binding.apply {
            buttonCreateAccount.isEnabled = state.enableViews
            tilEmail.isEnabled = state.enableViews
            tilUsername.isEnabled = state.enableViews
            tilPassword.isEnabled = state.enableViews
            tilRepeatPassword.isEnabled = state.enableViews

            fillError(tilEmail, state.emailErrorMessageRes)
            fillError(tilUsername, state.usernameErrorMessageRes)
            fillError(tilPassword, state.passwordErrorMessageRes)
            fillError(tilRepeatPassword, state.repeatPasswordMessageRes)

            progressBar.visibility = if (state.showProgress) View.VISIBLE else View.GONE
        }
    }

    private fun observeShowSuccessSignUpMessageEvent() = viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }

    private fun fillError(input: TextInputLayout, @StringRes stringRes: Int) {
        if (stringRes == SignUpViewModel.NO_ERROR_MESSAGE) {
            input.error = null
            input.isErrorEnabled = false
        } else {
            input.error = getString(stringRes)
            input.isErrorEnabled = true
        }
    }

    private fun observeGoBackEven() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }

    private fun getEmailArguments(): String? = args.email

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}