package com.mcmouse.nav_tabs.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentDashboardBinding
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.utils.viewModelCreator
import com.mcmouse.nav_tabs.views.DashBoardItemView

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding: FragmentDashboardBinding
        get() = _binding ?: throw NullPointerException("FragmentDashboardBinding is null")

    private val viewModel by viewModelCreator { DashboardViewModel(Repositories.boxesRepository) }

    private val boxClickListener = View.OnClickListener {
        val box = it.tag as Box
        val direction = DashboardFragmentDirections
            .actionDashboardFragmentToBoxFragment(
                box.id,
                getString(box.colorNameRes),
                box.colorValue
            )
        findNavController().navigate(direction)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        clearBoxViews()
        viewModel.boxes.observe(viewLifecycleOwner) { renderBoxes(it) }
    }

    private fun renderBoxes(boxes: List<Box>) {
        clearBoxViews()
        if (boxes.isEmpty()) {
            binding.tvNoBoxes.visibility = View.VISIBLE
            binding.boxesContainer.visibility = View.INVISIBLE
        } else {
            binding.tvNoBoxes.visibility = View.INVISIBLE
            binding.boxesContainer.visibility = View.VISIBLE
            createBoxes(boxes)
        }
    }

    private fun createBoxes(boxes: List<Box>) {
        val width = resources.getDimensionPixelSize(R.dimen.dashboard_item_width)
        val height = resources.getDimensionPixelSize(R.dimen.dashboard_item_height)

        val generatedIdentifiers = boxes.map { box ->
            val id = View.generateViewId()
            val dashboardItemView = DashBoardItemView(requireContext()).apply {
                setBox(box)
                this.id = id
                this.tag = box
                setOnClickListener(boxClickListener)
            }
            val params = ConstraintLayout.LayoutParams(width, height)
            binding.boxesContainer.addView(dashboardItemView, params)
            return@map id
        }.toIntArray()
        binding.viewFlow.referencedIds = generatedIdentifiers
    }

    private fun clearBoxViews() {
        binding.boxesContainer.removeViews(1, binding.root.childCount - 1)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}