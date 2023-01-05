package com.mcmouse88.fragmentdialog.level_2

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.mcmouse88.fragmentdialog.R
import com.mcmouse88.fragmentdialog.databinding.PartVolumeInputBinding

typealias CustomInputDialogListener = (requestKey: String, volume: Int) -> Unit

class CustomInputDialogFragment : DialogFragment() {

    private val volume: Int
        get() = requireArguments().getInt(ARG_VOLUME)

    private val requestKey: String
        get() = requireArguments().getString(ARG_REQUEST_KEY)
            ?: throw NullPointerException("requestKey in CustomInputDialogFragment is null")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = PartVolumeInputBinding.inflate(layoutInflater)
        dialogBinding.etVolumeInputLevelTwo.setText(volume.toString())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.volume_setup)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm, null)
            .create()
        dialog.setOnShowListener {
            dialogBinding.etVolumeInputLevelTwo.requestFocus()
            showKeyBoard(dialogBinding.etVolumeInputLevelTwo)

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredText = dialogBinding.etVolumeInputLevelTwo.text.toString()
                if (enteredText.isBlank()) {
                    dialogBinding.etVolumeInputLevelTwo.error = getString(R.string.empty_value)
                    return@setOnClickListener
                }
                val volume = enteredText.toIntOrNull()
                if (volume == null || volume > 100) {
                    dialogBinding.etVolumeInputLevelTwo.error = getString(R.string.invalid_value)
                    return@setOnClickListener
                }
                parentFragmentManager.setFragmentResult(
                    requestKey,
                    bundleOf(KEY_VOLUME_RESPONSE to volume)
                )
                dismiss()
            }
        }
        dialog.setOnDismissListener { hideKeyBoard(dialogBinding.etVolumeInputLevelTwo) }
        return dialog
    }

    private fun showKeyBoard(view: View) {
        view.post {
            getInputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyBoard(view: View) {
        getInputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getInputMethodManager(view: View): InputMethodManager {
        val context = view.context
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * Чтобы можно было переиспользовать диалоги для разных итемов на экране, то нужно использовать
     * разные ключи (ну и соответственно для каждого итема должен быть свой отдельный слушаетель)
     */
    companion object {
        private val TAG = CustomInputDialogFragment::class.java.simpleName
        private const val KEY_VOLUME_RESPONSE = "key_volume_response"
        private const val ARG_VOLUME = "arg_volume"
        private const val ARG_REQUEST_KEY = "arg_request_key"

        val DEFAULT_REQUEST_KEY = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager, volume: Int, requestKey: String) {
            val dialogFragment = CustomInputDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_VOLUME to volume,
                ARG_REQUEST_KEY to requestKey
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            requestKey: String,
            listener: CustomInputDialogListener
        ) {
            manager.setFragmentResultListener(requestKey, lifecycleOwner) { key, result ->
                listener.invoke(key, result.getInt(KEY_VOLUME_RESPONSE))
            }
        }
    }
}