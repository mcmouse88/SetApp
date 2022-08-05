package com.mcmouse88.choose_color.views.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.databinding.FragmentChangeColorBinding
import com.mcmouse88.choose_color.views.onTryAgain
import com.mcmouse88.choose_color.views.renderSimpleResult
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.BaseScreen
import com.mcmouse88.foundation.views.HasScreenTitle
import com.mcmouse88.foundation.views.screenViewModel
import kotlinx.coroutines.launch

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

        /**
         * Чтобы работать с Flow на стороне фрагмента, нам нужно у интерфейса [LifecycleOwner]
         * (который как мы помним умирает после вызова метода onDestroy) получить значение
         * lifecycleScope и запустить корутину. Внутри корутины вызвать метод [repeatOnLifecycle()].
         * Участок кода объявленный внутри метода будет выполняться с того метода жизненного цикла,
         * который укажем в параметрах и будет приостановлен в противоположном методе (пример
         * onStart -> onStop, onCreate -> onDestroy)
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { result ->
                    renderSimpleResult(binding.root, result) { viewState ->
                        adapter.items = viewState.colorsList
                        binding.buttonSave.visibility = if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                        binding.buttonCancel.visibility = if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE
                        binding.saveProgressBar.visibility = if (viewState.showProgressBar) View.VISIBLE else View.INVISIBLE
                    }
                }
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
