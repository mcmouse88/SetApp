package com.mcmouse88.open_box.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
     * но у нас он только один поэтому опускаем его), и второй самм bundle. При получении результата
     * от другого экрана с использованием [LiveData] и [SavedStateHandle], каждый раз при повороте
     * экрана будет приходить значение [LiveData], так как у нас тост сообщение, то оно будет
     * показываться при каждом повороте, поэтому после показа тост сообщения, нужно обнулить
     * занчение [LiveData], и проверять ее значение на null при показе тост сообщения.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRootBinding.bind(view)

        binding.buttonOpenGreenBox.setOnClickListener {
            openBox(Color.rgb(200, 255, 200), getString(R.string.green))
        }

        binding.buttonOpenYellowBox.setOnClickListener {
            openBox(Color.rgb(255, 255, 200), getString(R.string.yellow))
        }

        val liveData = findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(BoxFragment.EXTRA_RANDOM_NUMBER)

        liveData?.observe(viewLifecycleOwner) { number ->
            if (number != null) {
                Toast.makeText(
                    requireContext(),
                    "Generated number: $number",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            liveData.value = null
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /**
     * Чтобы переадвать аргументы через safe_args нужно использвать дирекшены, которые
     * автоматически генерируются при подключении плагина.
     */
    private fun openBox(color: Int, colorName: String) {
        val direction = RootFragmentDirections.actionRootFragmentToBoxFragment(color, colorName)

        findNavController().navigate(direction)
    }
}