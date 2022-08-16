package com.mcmouse.nav_tabs.screens.main.tabs.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentSettingsBinding
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw NullPointerException("FragmentSettingsBinding is null")

    private val viewModel by viewModelCreator { SettingsViewModel(Repositories.boxesRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        val adapter = setupList()
        viewModel.boxSetting.observe(viewLifecycleOwner) { adapter.renderSettings(it) }

        viewModel.showErrorMessageEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupList(): SettingsAdapter {
        binding.rvSettingsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SettingsAdapter(viewModel)
        binding.rvSettingsList.adapter = adapter
        return adapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}