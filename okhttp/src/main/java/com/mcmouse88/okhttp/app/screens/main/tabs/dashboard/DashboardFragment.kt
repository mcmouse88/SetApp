package com.mcmouse88.okhttp.app.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.model.boxes.entities.Box
import com.mcmouse88.okhttp.app.screens.base.BaseFragment
import com.mcmouse88.okhttp.app.utiils.observeResult
import com.mcmouse88.okhttp.app.views.DashboardItemView
import com.mcmouse88.okhttp.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    override val viewModel by viewModels<DashboardViewModel>()

    private var _binding: FragmentDashboardBinding? = null
    private val binding: FragmentDashboardBinding
        get() = _binding ?: throw NullPointerException("FragmentDashboardBinding is null")

    private val boxClickListener = View.OnClickListener {
        val box = it.tag as Box
        val direction = DashboardFragmentDirections.actionDashboardFragmentToBoxFragment(
            boxId = box.id,
            colorName = box.colorName,
            colorValue = box.colorValue
        )
        findNavController().navigate(direction)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        clearBoxViews()
        binding.resultView.setTryAgainAction { viewModel.reload() }
        viewModel.boxes.observeResult(this, view, binding.resultView) {
            renderBoxes(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
            val dashboardItemView = DashboardItemView(requireContext())
            dashboardItemView.setBox(box)
            dashboardItemView.id = id
            dashboardItemView.tag = box
            dashboardItemView.setOnClickListener(boxClickListener)
            val params = ConstraintLayout.LayoutParams(width, height)
            binding.boxesContainer.addView(dashboardItemView, params)
            return@map id
        }.toIntArray()
        binding.flowView.referencedIds = generatedIdentifiers
    }

    private fun clearBoxViews() {
        if (binding.boxesContainer.childCount > 1) {
            binding.boxesContainer.removeViews(1, binding.boxesContainer.childCount - 1)
        }
    }
}