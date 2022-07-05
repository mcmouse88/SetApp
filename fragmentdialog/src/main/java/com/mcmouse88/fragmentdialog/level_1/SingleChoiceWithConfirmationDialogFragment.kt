package com.mcmouse88.fragmentdialog.level_1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.mcmouse88.fragmentdialog.R
import com.mcmouse88.fragmentdialog.entities.AvailableVolumeValues

class SingleChoiceWithConfirmationDialogFragment : DialogFragment() {

    private val volume: Int
        get() = requireArguments().getInt(ARG_VOLUME)

    /**
     * свойство listView само сохраняет информацию, по этому здесь можно не переопределять
     * метод [onSaveInstanceState]
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val volumeItem = AvailableVolumeValues.createVolumeValues(volume)
        val volumeTextItem = volumeItem.values.map { getString(R.string.volume_description, it) }
            .toTypedArray()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(volumeTextItem, volumeItem.currentIndex, null)
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                val index = (dialog as AlertDialog).listView.checkedItemPosition
                val volume = volumeItem.values[index]
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_VOLUME_RESPONSE to volume)
                )
            }
            .create()
    }

    companion object {
        private val TAG = SingleChoiceWithConfirmationDialogFragment::class.java.simpleName
        private const val KEY_VOLUME_RESPONSE = "key_volume_response"
        private const val ARG_VOLUME = "arg_volume"

        val REQUEST_KEY = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager, volume: Int) {
            val dialogFragment = SingleChoiceWithConfirmationDialogFragment()
            dialogFragment.arguments = bundleOf(ARG_VOLUME to volume)
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Int) -> Unit
        ) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) {_, result ->
                listener.invoke(result.getInt(KEY_VOLUME_RESPONSE))
            }
        }
    }
}