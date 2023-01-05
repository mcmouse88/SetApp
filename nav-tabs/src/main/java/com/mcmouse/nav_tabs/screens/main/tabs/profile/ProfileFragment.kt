package com.mcmouse.nav_tabs.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentProfileBinding
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.utils.findTopNavController
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw NullPointerException("FragmentProfileBinding is null")

    private val viewModel by viewModelCreator { ProfileViewModel(Repositories.accountsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.buttonEditProfile.setOnClickListener { onEditProfileButtonPressed() }
        binding.buttonLogout.setOnClickListener { onLogoutButtonPressed() }

        observeAccountDetails()
        observeRestartAppFromLoginScreenEvent()
    }

    /**
     * Нам нужно, чтобы экран редактирования пользователся запускался не внутри вкладок, а
     * поверх них, плэтому метод [findNavController] нам не подходит. Что значит, запустить поверх,
     * так как у нас есть [BottomNavigationView], у которого внизу есть меню, так вот чтобы это
     * меню не показывалось на определнных экранах, нужно запускать фрагмент поверх вкладок.
     * Экраны, которые запускаются поверх вкладок и не имеют [BottomNavigationView] у нас
     * вынесены в отдельный граф [main_graph]. Чтобы запускать фрагмент поверх вкладок,
     * мы написали свою extension функцию [findTopNavController]
     */
    private fun onEditProfileButtonPressed() {
        findTopNavController().navigate(R.id.editProfileFragment)
    }

    private fun onLogoutButtonPressed() {
        viewModel.logout()
    }

    private fun observeAccountDetails() {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        viewModel.account.observe(viewLifecycleOwner) { account ->
            if (account == null) return@observe
            binding.apply {
                tvEmail.text = account.email
                tvUsername.text = account.username
                tvCreatedAt.text =
                    if (account.createdAt == Account.UNKNOWN_CREATED_AT) getString(R.string.placeholder)
                    else formatter.format(Date(account.createdAt))
            }
        }
    }

    private fun observeRestartAppFromLoginScreenEvent() {
        viewModel.restartFromLoginEvent.observeEvent(viewLifecycleOwner) {
            findTopNavController().navigate(R.id.signInFragment, null, navOptions {
                popUpTo(R.id.tabsFragment) {
                    inclusive = true
                }
            })
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}