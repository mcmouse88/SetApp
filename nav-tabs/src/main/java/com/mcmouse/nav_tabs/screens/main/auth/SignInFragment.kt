package com.mcmouse.nav_tabs.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentSignInBinding
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.toCharArray
import com.mcmouse.nav_tabs.utils.viewModelCreator

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding ?: throw NullPointerException("FragmentSignInBinding is null")

    private val viewModel by viewModelCreator { SignInViewModel(Repositories.accountsRepository) }

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

    private fun onSignInButtonPressed() {
        viewModel.signIn(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toCharArray()
        )
    }

    private fun onSignUpButtonPressed() {
        val email = binding.etEmail.text.toString()
        val emailArg = email.ifBlank { null }

        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment(emailArg)
        findNavController().navigate(direction)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.tilEmail.error =
            if (it.emptyEmailError) getString(R.string.field_is_empty) else null
        binding.tilPassword.error =
            if (it.emptyPasswordError) getString(R.string.field_is_empty) else null

        binding.etEmail.isEnabled = it.enableViews
        binding.etPassword.isEnabled = it.enableViews
        binding.buttonSignIn.isEnabled = it.enableViews
        binding.buttonSignUp.isEnabled = it.enableViews
        binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.GONE
    }

    private fun observeShowAuthErrorMessageEvent() = viewModel.showAuthErrorToastEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), R.string.invalid_email_or_password, Toast.LENGTH_SHORT).show()
    }

    private fun observeClearPasswordEvent() = viewModel.clearPasswordEvent.observeEvent(viewLifecycleOwner) {
        binding.etPassword.text?.clear()
    }

    /**
     * Чтобы при переходе на другой экран исключить из стека предыдущие экраны, то при навигации
     * через navController нужно передать параметр [navOptions], в котором вызвать метод
     * [popUpTo()], куда передать идентификатор текущего фрагмента, а также указать флаг
     * inclusive, где true это исключить также текущий экран из стека, и false оставить его в стеке.
     * Пример:
     * ```kotlin
     * findNavController().navigate(R.id.action_signInFragment_to_tabsFragment, null, navOptions {
     *     popUpTo(R.id.signInFragment) {
     *         inclusive = true
     *     }
     * })
     * ```
     * Но также это можно указать и в самом графе навигации в XML файле, что собственно мы
     * и сделаем.
     */
    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}