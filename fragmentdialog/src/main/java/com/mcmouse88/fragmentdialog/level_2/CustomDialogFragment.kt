package com.mcmouse88.fragmentdialog.level_2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.mcmouse88.fragmentdialog.R
import com.mcmouse88.fragmentdialog.databinding.PartVolumeBinding

class CustomDialogFragment : DialogFragment() {

    private val volume: Int
        get() = requireArguments().getInt(ARG_VOLUME)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       val dialogBinding = PartVolumeBinding.inflate(layoutInflater)
        dialogBinding.seekbarVolume.progress = volume
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.volume_setup)
            .setMessage(R.string.volume_setup_message)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm) {_, _ ->
                val newVolume = dialogBinding.seekbarVolume.progress
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_VOLUME_RESPONSE to newVolume)
                )
            }
            .create()
    }

    companion object {
        private val TAG = CustomDialogFragment::class.java.simpleName
        private const val KEY_VOLUME_RESPONSE = "key_volume_response"
        private const val ARG_VOLUME = "arg_volume"

        val REQUEST_KEY = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager, volume: Int) {
            val dialogFragment = CustomDialogFragment()
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