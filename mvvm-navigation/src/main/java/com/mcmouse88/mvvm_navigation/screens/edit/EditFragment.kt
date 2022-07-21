package com.mcmouse88.mvvm_navigation.screens.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcmouse88.mvvm_navigation.databinding.FragmentEditBinding
import com.mcmouse88.mvvm_navigation.screens.base.BaseFragment
import com.mcmouse88.mvvm_navigation.screens.base.BaseScreen
import com.mcmouse88.mvvm_navigation.screens.base.BaseViewModel
import com.mcmouse88.mvvm_navigation.screens.base.screenViewModel

class EditFragment : BaseFragment() {

    /**
     * Тоже класс для запуска [EditFragment], но в нем уже будут присутствовать параметры,
     * необходимые для данного фрагмента, а именно объект типа String, который мы будем
     * передавать при запуске данного фрагмента.
     */
    class Screen(
        val initialValue: String
    ) : BaseScreen

    override val viewModel by screenViewModel<EditViewModel>()

    private var _binding: FragmentEditBinding? = null
    private val binding: FragmentEditBinding
        get() = _binding ?: throw NullPointerException("FragmentEditBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        viewModel.initialMessageEvent.observe(viewLifecycleOwner) {
            it.getValue()?.let { message ->
                binding.editTextValue.setText(message)
            }
        }

        binding.buttonSave.setOnClickListener {
            viewModel.onSavePressed(binding.editTextValue.text.toString())
        }

        binding.buttonCancel.setOnClickListener { viewModel.onCancelPressed() }

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}