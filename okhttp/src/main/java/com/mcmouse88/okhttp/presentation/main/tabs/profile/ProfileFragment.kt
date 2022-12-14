package com.mcmouse88.okhttp.presentation.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.databinding.FragmentProfileBinding
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.presentation.base.BaseFragment
import com.mcmouse88.okhttp.utils.findTopNavController
import com.mcmouse88.okhttp.utils.observeResult
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    override val viewModel by viewModels<ProfileViewModel>()

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw NullPointerException("FragmentProfileBinding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.buttonEditProfile.setOnClickListener { onEditProfileButtonPressed() }
        binding.buttonLogout.setOnClickListener { logout() }
        binding.resultView.setTryAgainAction { viewModel.reload() }

        observeAccountDetails()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeAccountDetails() {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        viewModel.account.observeResult(this, binding.root, binding.resultView) { account ->
            binding.tvEmail.text = account.email
            binding.tvUsername.text = account.username
            binding.tvCreatedAt.text = if (account.createdAt == Account.UNKNOWN_CREATE_AT) {
                getString(R.string.placeholder)
            } else formatter.format(Date(account.createdAt))
        }
    }

    private fun onEditProfileButtonPressed() {
        findTopNavController().navigate(R.id.editProfileFragment)
    }
}