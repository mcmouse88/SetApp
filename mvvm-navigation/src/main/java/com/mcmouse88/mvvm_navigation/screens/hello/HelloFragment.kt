package com.mcmouse88.mvvm_navigation.screens.hello

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcmouse88.mvvm_navigation.databinding.FragmentHelloBinding
import com.mcmouse88.mvvm_navigation.screens.base.BaseFragment
import com.mcmouse88.mvvm_navigation.screens.base.BaseScreen
import com.mcmouse88.mvvm_navigation.screens.base.BaseViewModel
import com.mcmouse88.mvvm_navigation.screens.base.screenViewModel

class HelloFragment : BaseFragment() {

    /**
     * Определим класс [Screen] внутри фрагмента, при помощи которого мы будем запускать
     * [HelloFragment]
     */
    class Screen : BaseScreen

    override val viewModel by screenViewModel<HelloViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHelloBinding.inflate(inflater, container, false)

        binding.buttonEdit.setOnClickListener { viewModel.onEditPressed() }
        viewModel.currentMessage.observe(viewLifecycleOwner) {
            binding.tvValue.text = it
        }

        return binding.root
    }
}