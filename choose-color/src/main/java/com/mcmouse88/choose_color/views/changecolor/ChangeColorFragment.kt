package com.mcmouse88.choose_color.views.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.databinding.FragmentChangeColorBinding
import com.mcmouse88.choose_color.views.collectFlow
import com.mcmouse88.choose_color.views.onTryAgain
import com.mcmouse88.choose_color.views.renderSimpleResult
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.BaseScreen
import com.mcmouse88.foundation.views.HasScreenTitle
import com.mcmouse88.foundation.views.screenViewModel

class ChangeColorFragment : BaseFragment(), HasScreenTitle {

    class Screen(
        val currentColorId: Long
    ) : BaseScreen

    override val viewModel by screenViewModel<ChangeColorViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        val adapter = ColorsAdapter(viewModel)
        setupLayoutManager(binding, adapter)

        binding.buttonSave.setOnClickListener { viewModel.onSavePressed() }
        binding.buttonCancel.setOnClickListener { viewModel.onCancelPressed() }

        collectFlow(viewModel.viewState) { result ->
            renderSimpleResult(binding.root, result) { viewState ->
                adapter.items = viewState.colorsList
                binding.buttonSave.visibility =
                    if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                binding.buttonCancel.visibility =
                    if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE

                binding.groupSaveProgress.visibility =
                    if (viewState.showProgressBar) View.VISIBLE else View.INVISIBLE
                binding.saveProgressBar.progress = viewState.saveProgressPercentage
                binding.tvSavingPercentage.text = viewState.saveProgressPercentageMessage
            }
        }

        viewModel.screenTitle.observe(viewLifecycleOwner) {
            notifyScreenUpdates()
        }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val width = binding.root.width
                    val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                    val column = width / itemWidth
                    binding.rvColor.adapter = adapter
                    binding.rvColor.layoutManager = GridLayoutManager(requireContext(), column)
                }
            })
    }
}
