package com.mcmouse88.okhttp.presentation.main.tabs.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.databinding.FragmentSettingsBinding
import com.mcmouse88.okhttp.presentation.base.BaseFragment
import com.mcmouse88.okhttp.utils.observeResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override val viewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw NullPointerException("FragmentSettingsBinding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.resultView.setTryAgainAction { viewModel.tryAgain() }
        val adapter = setupList()
        viewModel.boxSetting.observeResult(this, view, binding.resultView) {
            adapter.renderSettings(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupList(): SettingsAdapter {
        binding.rvSettingsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SettingsAdapter(viewModel)
        binding.rvSettingsList.adapter = adapter
        return adapter
    }
 }