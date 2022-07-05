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

class MultipleChoiceDialogFragment : DialogFragment() {

    private val color: Int
        get() = requireArguments().getInt(ARG_COLOR)

    /**
     * Во фрагменте с множественным выбором какие-то отдельные переменные стоит использовать
     * только ссовместно с методом [onSaveInstanceState], так как при изменении конфигурации
     * выбранные значения остануться в саписке диалога, но по акту будут иметь значения, которые
     * были при старте экрана, в связи с этим могут возникнуть ситуации, в которых мы получим
     * не то, что ожидаем, но мы используем то, что свойство listview запоминает значение, и
     * передадим эту логику при создании диалога метод [booleanToColorComponent], который
     * пройдет по списку listview и проверит, какие элементы выбраны, а какие нет, и вернет
     * соответствующий цвет. Также чтобы не было конфликта с [onSaveInstanceState], мы проверяем
     * его на null, и если не null то возвращаем false? тем самым давая возможность listview
     * самому восстановить состояние до изменения конфигурации
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val colorItem = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkBoxes = colorComponents
            .map { it > 0 && savedInstanceState == null }
            .toBooleanArray()
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItem, checkBoxes) { dialog, _, _ ->
                val checkedPosition = (dialog as AlertDialog).listView.checkedItemPositions
                val color = Color.rgb(
                    booleanToColorComponent(checkedPosition[0]),
                    booleanToColorComponent(checkedPosition[1]),
                    booleanToColorComponent(checkedPosition[2])
                )
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_COLOR_RESPONSE to color)
                )
            }
            .setPositiveButton(R.string.action_close, null)
            .create()
    }

    private fun booleanToColorComponent(value: Boolean): Int {
        return if (value) 255 else 0
    }

    companion object {
        private val TAG = MultipleChoiceDialogFragment::class.java.simpleName
        private const val ARG_COLOR = "args_color"
        private val REQUEST_KEY = "$TAG:defaultRequestKEY"
        private const val KEY_COLOR_RESPONSE = "key_color_response"

        fun show(manager: FragmentManager, color: Int) {
            val dialogFragment = MultipleChoiceDialogFragment()
            dialogFragment.arguments = bundleOf(ARG_COLOR to color)
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