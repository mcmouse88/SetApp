package com.mcmouse88.okhttp.app.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.model.accounts.entities.SignUpData
import com.mcmouse88.okhttp.app.screens.base.BaseFragment
import com.mcmouse88.okhttp.app.utiils.observeEvent
import com.mcmouse88.okhttp.databinding.FragmentSignUpBinding

class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    override val viewModel by viewModels<SignUpViewModel>()

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding ?: throw NullPointerException("FragmentSignUpBinding is null")

    private val args by navArgs<SignUpFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)

        binding.btnCreateAccount.setOnClickListener { onCreateAccountButtonPressed() }

        if (savedInstanceState == null && getEmailArgument() != null) {
            binding.etEmail.setText(getEmailArgument())
        }

        observeState()
        observeGoBackEvent()
        observeShowSuccessSignUpMessageEvent()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onCreateAccountButtonPressed() {
        val signInData = SignUpData(
            email = binding.etEmail.text.toString(),
            username = binding.etUsername.text.toString(),
            password = binding.etPassword.text.toString(),
            repeatPassword = binding.etRepeatPassword.text.toString()
        )
        viewModel.signUp(signInData)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        binding.apply {
            btnCreateAccount.isEnabled = state.enableViews
            tilEmail.isEnabled = state.enableViews
            tilUsername.isEnabled = state.enableViews
            tilPassword.isEnabled = state.enableViews
            tilRepeatPassword.isEnabled = state.enableViews

            fillError(tilEmail, state.emailErrorMessageRes)
            fillError(tilUsername, state.userNameErrorMessageRes)
            fillError(tilPassword, state.passwordErrorMessageRes)
            fillError(tilRepeatPassword, state.repeatPasswordErrorMessageRes)

            progressBar.visibility = if (state.showProgress) View.VISIBLE else View.INVISIBLE
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

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }

    private fun getEmailArgument(): String? = args.email
}