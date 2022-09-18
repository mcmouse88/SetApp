package com.mcmouse88.okhttp.app.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.screens.base.BaseFragment
import com.mcmouse88.okhttp.app.utiils.observeEvent
import com.mcmouse88.okhttp.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment(R.layout.fragment_edit_profile) {

    override val viewModel by viewModels<EditProfileViewModel>()

    private var _binding: FragmentEditProfileBinding? = null
    private val binding: FragmentEditProfileBinding
        get() = _binding ?: throw NullPointerException("FragmentEditProfileBinding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        binding.buttonSave.setOnClickListener { onSaveButtonPressed() }
        binding.buttonCancel.setOnClickListener { onCancelButtonPressed() }

        if (savedInstanceState == null) listenInitialUserNameEvent()
        observeGoBackEvent()
        observeSaveInProgress()
        observeEmptyFieldErrorEvent()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onSaveButtonPressed() {
        viewModel.saveUserName(binding.etUsername.text.toString())
    }

    private fun observeSaveInProgress() =
        viewModel.saveInProgress.observe(viewLifecycleOwner) { progress ->
            binding.apply {
                progressBar.visibility = if (progress) View.VISIBLE else View.INVISIBLE
                buttonSave.isEnabled = progress.not()
                tilUsername.isEnabled = progress.not()
            }
        }

    private fun listenInitialUserNameEvent() =
        viewModel.initialUserNameEvent.observeEvent(viewLifecycleOwner) {
            binding.etUsername.setText(it)
        }

    private fun observeEmptyFieldErrorEvent() =
        viewModel.showErrorEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

    private fun onCancelButtonPressed() {
        findNavController().popBackStack()
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
}