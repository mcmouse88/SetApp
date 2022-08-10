package com.mcmouse88.open_box.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.mcmouse88.open_box.R
import com.mcmouse88.open_box.databinding.FragmentBoxBinding
import kotlin.random.Random

class BoxFragment : Fragment(R.layout.fragment_box) {

    private var _binding: FragmentBoxBinding? = null
    private val binding: FragmentBoxBinding
        get() = _binding ?: throw NullPointerException("FragmentBoxBinding is null")

    /**
     * Чтобы возвращать результат на предыдущий франмент можно воспользоватся следующим способом:
     * ```kotlin
     * findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "value")
     * ```
     * где в метод set нужно передать пару ключ значение. Чтобы получить результат на предыдущем
     * фрагменте нужно воспользоваться следующей конструкцией
     * ```kotlin
     * findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("key")?.observe(viewLifecycleOwner) {
     *     // some code
     * }
     * где в методе get нужно указать ключ, который был указан при отправке результата.
     * Мы же используем метод Fragment Result Api, который собственно для этого и предназначен.
     * У свойства parentFragmentManager вызываем метод setFragmentResult, куда передаем request
     * code и bundle со значением.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBoxBinding.bind(view)

        val color = requireArguments().getInt(ARG_COLOR)
        binding.root.setBackgroundColor(color)

        binding.buttonOpenSecret.setOnClickListener {
            findNavController().navigate(R.id.action_boxFragment_to_secretFragment)
        }

        binding.buttonGenerateNumber.setOnClickListener {
            val number = Random.nextInt(100)
            parentFragmentManager.setFragmentResult(REQUEST_CODE, bundleOf(EXTRA_RANDOM_NUMBER to number))
            findNavController().popBackStack()
        }

        binding.buttonGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_COLOR = "arg_color"
        const val ARG_COLOR_NAME = "arg_color_name"

        const val REQUEST_CODE = "random_number"
        const val EXTRA_RANDOM_NUMBER = "extra_random_number"
    }
}