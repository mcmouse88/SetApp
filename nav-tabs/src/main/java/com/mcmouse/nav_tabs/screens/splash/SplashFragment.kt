package com.mcmouse.nav_tabs.screens.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentSplashBinding
import com.mcmouse.nav_tabs.screens.main.MainActivity
import com.mcmouse.nav_tabs.screens.main.MainActivityArgs
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator

class   SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding: FragmentSplashBinding
        get() = _binding ?: throw NullPointerException("FragmentSplashBinding")

    private val viewModel by viewModelCreator { SplashViewModel(Repositories.accountsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)

        renderAnimation()

        viewModel.launchMainScreenEvent.observeEvent(viewLifecycleOwner) {
            launchMainScreen(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    /**
     * Флаги [FLAG_ACTIVITY_NEW_TASK] и [FLAG_ACTIVITY_CLEAR_TASK] полностью закрывают активити
     * при переходе, и она не будет хранится в стеке.
     */
    private fun launchMainScreen(isSignedIn: Boolean) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtras(MainActivityArgs(isSignedIn).toBundle())
        }

        startActivity(intent)
    }

    private fun renderAnimation() {
        binding.loadingIndicator.alpha = 0f
        binding.loadingIndicator.animate()
            .alpha(0.7f)
            .setDuration(1_000)
            .start()

        binding.tvPleaseWait.alpha = 0f
        binding.tvPleaseWait.animate()
            .alpha(1f)
            .setStartDelay(500)
            .setDuration(1_000)
            .start()
    }
}