package com.mcmouse.nav_tabs.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentEditProfileBinding
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding: FragmentEditProfileBinding
        get() = _binding ?: throw NullPointerException("FragmentEditProfileBinding is null")

    private val viewModel by viewModelCreator { EditProfileViewModel(Repositories.accountsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        binding.buttonSave.setOnClickListener { onSaveButtonPressed() }
        binding.buttonCancel.setOnClickListener { onCancelButtonPressed() }

        if (savedInstanceState == null) {
            listenInitialUserNameEvent()
        }

        observeGoBackEvent()
        observeSaveInProgress()
        observeEmptyFieldErrorEvent()
    }

    private fun onSaveButtonPressed() {
        viewModel.saveUsername(binding.etUsername.text.toString())
    }

    private fun onCancelButtonPressed() {
        findNavController().popBackStack()
    }

    private fun listenInitialUserNameEvent() = viewModel.initialUserNameEvent.observeEvent(viewLifecycleOwner) {
        binding.etUsername.setText(it)
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }

    private fun observeSaveInProgress() = viewModel.saveInProgress.observe(viewLifecycleOwner) {
        binding.apply {
            if (it) {
                progressBar.visibility = View.VISIBLE
                buttonSave.isEnabled = false
                tilUsername.isEnabled = false
            } else {
                progressBar.visibility = View.INVISIBLE
                buttonSave.isEnabled = true
                tilUsername.isEnabled = true
            }
        }
    }

    private fun observeEmptyFieldErrorEvent() = viewModel.showEmptyFieldErrorEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), R.string.field_is_empty, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}