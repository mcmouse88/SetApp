package com.mcmouse88.choose_color.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcmouse88.choose_color.databinding.FragmentCurrentColorBinding
import com.mcmouse88.choose_color.views.renderSimpleResult
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.BaseScreen
import com.mcmouse88.foundation.views.onTryAgain
import com.mcmouse88.foundation.views.screenViewModel

class CurrentColorFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentColorBinding.inflate(inflater, container, false)

        /**
         * обработка результата в условиях LiveData
         */
        viewModel.currentColor.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    binding.colorView.setBackgroundColor(it.value)
                }
            )
        }

        binding.buttonChangeColor.setOnClickListener { viewModel.changeColor() }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }
}