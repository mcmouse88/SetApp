package com.mcmouse.nav_tabs.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentBoxBinding
import com.mcmouse.nav_tabs.utils.observeEvent
import com.mcmouse.nav_tabs.utils.viewModelCreator
import com.mcmouse.nav_tabs.views.DashBoardItemView

class BoxFragment : Fragment(R.layout.fragment_box) {

    private var _binding: FragmentBoxBinding? = null
    private val binding: FragmentBoxBinding
        get() = _binding ?: throw NullPointerException("FragmentBoxBinding is null")

    private val viewModel by viewModelCreator {
        BoxViewModel(
            getBoxId(),
            Repositories.boxesRepository
        )
    }

    private val args by navArgs<BoxFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBoxBinding.bind(view)

        binding.root.setBackgroundColor(DashBoardItemView.getBackgroundColor(getColorValue()))
        binding.tvBox.text = getString(R.string.this_is_box, getColorName())
        binding.buttonGoBack.setOnClickListener { onGoBackButtonPressed() }

        listenShouldExitEvent()
    }

    private fun onGoBackButtonPressed() {
        findNavController().popBackStack()
    }

    private fun listenShouldExitEvent() =
        viewModel.shouldExitEvent.observeEvent(viewLifecycleOwner) { shouldExit ->
            if (shouldExit) findNavController().popBackStack()
        }

    private fun getBoxId(): Long = args.boxId

    private fun getColorValue(): Int = args.colorValue

    private fun getColorName(): String = args.colorName

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}