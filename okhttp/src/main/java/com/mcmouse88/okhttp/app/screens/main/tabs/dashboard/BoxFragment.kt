package com.mcmouse88.okhttp.app.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.screens.base.BaseFragment
import com.mcmouse88.okhttp.app.utiils.observeEvent
import com.mcmouse88.okhttp.app.utiils.viewModelCreator
import com.mcmouse88.okhttp.app.views.DashboardItemView
import com.mcmouse88.okhttp.databinding.FragmentBoxBinding

class BoxFragment : BaseFragment(R.layout.fragment_box) {

    override val viewModel by viewModelCreator { BoxViewModel(getBoxId()) }

    private var _binding: FragmentBoxBinding? = null
    private val binding: FragmentBoxBinding
        get() = _binding ?: throw NullPointerException("FragmentBoxBinding is null")

    private val args by navArgs<BoxFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBoxBinding.bind(view)

        binding.root.setBackgroundColor(DashboardItemView.getBackgroundColor(getColorValue()))
        binding.tvBox.text = getString(R.string.this_is_box, getColorName())

        binding.btnGoBack.setOnClickListener { onGoBackButtonPressed() }
        listenShouldExitEvent()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onGoBackButtonPressed() {
        findNavController().popBackStack()
    }

    private fun listenShouldExitEvent() = viewModel.shouldExitEvent.observeEvent(viewLifecycleOwner) {
        if (it) findNavController().popBackStack()
    }

    private fun getBoxId(): Long = args.boxId

    private fun getColorValue(): Int = args.colorValue

    private fun getColorName(): String = args.colorName
}