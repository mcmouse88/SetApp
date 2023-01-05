package com.mcmouse88.box_quiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.mcmouse88.box_quiz.Options
import com.mcmouse88.box_quiz.R
import com.mcmouse88.box_quiz.contract.CustomAction
import com.mcmouse88.box_quiz.contract.HasCustomAction
import com.mcmouse88.box_quiz.contract.HasCustomTitle
import com.mcmouse88.box_quiz.contract.navigator
import com.mcmouse88.box_quiz.databinding.FragmentOptionsBinding

class OptionsFragment : Fragment(), HasCustomTitle, HasCustomAction {

    private var _binding: FragmentOptionsBinding? = null
    private val binding: FragmentOptionsBinding
        get() = _binding ?: throw RuntimeException("FragmentOptionsBinding is null")

    private lateinit var options: Options
    private lateinit var boxCountItems: List<BoxCountItem>
    private lateinit var adapter: ArrayAdapter<BoxCountItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        options = savedInstanceState?.getParcelable(KEY_OPTIONS)
            ?: arguments?.getParcelable(ARGS_OPTIONS)
                    ?: throw IllegalArgumentException("You need to specify EXTRA_OPTIONS to launch this activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)

        setupSpinner()
        setupCheckBox()
        updateUI()

        binding.btConfirm.setOnClickListener { onConfirmPressed() }
        binding.btCancel.setOnClickListener { onCancelPressed() }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_OPTIONS, options)
    }

    override fun getTitleRes() = R.string.options

    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.ic_done,
            textRes = R.string.icon_done,
            onCustomAction = {
                onConfirmPressed()
            }
        )
    }

    private fun setupSpinner() {
        boxCountItems = (1..6).map {
            BoxCountItem(it, resources.getQuantityString(R.plurals.boxes, it, it))
        }
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            boxCountItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        binding.spinnerBoxCount.adapter = adapter
        binding.spinnerBoxCount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val count = boxCountItems[position].count
                    options = options.copy(boxCount = count)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun setupCheckBox() {
        binding.cbEnabledTimer.setOnClickListener {
            options = options.copy(
                isTimerEnabled = binding.cbEnabledTimer.isChecked
            )
        }
    }

    private fun updateUI() {
        binding.cbEnabledTimer.isChecked = options.isTimerEnabled

        val currentIndex = boxCountItems.indexOfFirst { it.count == options.boxCount }
        binding.spinnerBoxCount.setSelection(currentIndex)
    }

    private fun onCancelPressed() {
        navigator().goBack()
    }

    private fun onConfirmPressed() {
        navigator().publishResult(options)
        navigator().goBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGS_OPTIONS = "args_options"
        private const val KEY_OPTIONS = "key_options"

        fun createArgs(options: Options) = bundleOf(ARGS_OPTIONS to options)
    }

    class BoxCountItem(
        val count: Int,
        private val optionsTitle: String
    ) {
        override fun toString(): String {
            return optionsTitle
        }
    }
}