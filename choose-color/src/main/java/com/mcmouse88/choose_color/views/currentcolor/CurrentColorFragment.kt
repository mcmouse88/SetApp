package com.mcmouse88.choose_color.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcmouse88.choose_color.databinding.FragmentCurrentColorBinding
import com.mcmouse88.choose_color.views.base.BaseFragment
import com.mcmouse88.choose_color.views.base.BaseScreen
import com.mcmouse88.choose_color.views.base.screenViewModel

class CurrentColorFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentColorBinding.inflate(inflater, container, false)

        viewModel.currentColor.observe(viewLifecycleOwner) {
            binding.colorView.setBackgroundColor(it.value)
        }

        binding.buttonChangeColor.setOnClickListener { viewModel.changeColor() }

        return binding.root
    }
}