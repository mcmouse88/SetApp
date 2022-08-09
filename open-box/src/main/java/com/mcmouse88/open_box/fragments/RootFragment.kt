package com.mcmouse88.open_box.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcmouse88.open_box.R
import com.mcmouse88.open_box.databinding.FragmentRootBinding

class RootFragment : Fragment(R.layout.fragment_root) {

    private var _binding: FragmentRootBinding? = null
    private val binding: FragmentRootBinding
        get() = _binding ?: throw NullPointerException("FragmentRootBinding is null")

    /**
     * Чтобы принять результат с другого фрагмента, у parentFragmentManager нужно вызывать метод
     * setFragmentResultListener, куда нужно передать request code и LifeCycleOwner, в лямбду
     * попадает два параметра, первый это сам request code(на случай обработки разных результатов,
     * но у нас он только один поэтому опускаем его), и второй самм bundle.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRootBinding.bind(view)

        binding.buttonOpenGreenBox.setOnClickListener {
            openBox(Color.rgb(200, 255, 200))
        }

        binding.buttonOpenYellowBox.setOnClickListener {
            openBox(Color.rgb(255, 255, 200))
        }

        parentFragmentManager.setFragmentResultListener(BoxFragment.REQUEST_CODE, viewLifecycleOwner) { _, data ->
            val number = data.getInt(BoxFragment.EXTRA_RANDOM_NUMBER)
            Toast.makeText(requireContext(), "Generated number: $number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun openBox(color: Int) {
        findNavController().navigate(
            R.id.action_rootFragment_to_boxFragment,
            bundleOf(BoxFragment.ARG_COLOR to color)
        )
    }
}