package com.mcmouse88.fragmentdialog.level_1

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.mcmouse88.fragmentdialog.R

class MultipleChoiceWithConfirmationDialogFragment : DialogFragment() {

    private val color: Int
        get() = requireArguments().getInt(ARGS_COLOR)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val colorItems = resources.getStringArray(R.array.colors)
        val colorComponent = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkBoxes = colorComponent
            .map { it > 0 && savedInstanceState == null }
            .toBooleanArray()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItems, checkBoxes, null)
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                val checkedPosition = (dialog as AlertDialog).listView.checkedItemPositions
                val color = Color.rgb(
                    booleanToColorComponent(checkedPosition[0]),
                    booleanToColorComponent(checkedPosition[1]),
                    booleanToColorComponent(checkedPosition[2]),
                )
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_COLOR_RESPONSE to color)
                )
            }
            .create()
    }

    private fun booleanToColorComponent(value: Boolean): Int {
        return if (value) 255 else 0
    }

    companion object {
        private val TAG = MultipleChoiceWithConfirmationDialogFragment::class.java.simpleName
        private const val ARGS_COLOR = "args_color"
        private const val KEY_COLOR_RESPONSE = "key_color_response"

        val REQUEST_KEY = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager, color: Int) {
            val dialogFragment = MultipleChoiceWithConfirmationDialogFragment()
            dialogFragment.arguments = bundleOf(ARGS_COLOR to color)
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Int) -> Unit
        ) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, result ->
                listener.invoke(result.getInt(KEY_COLOR_RESPONSE))
            }
        }
    }
}