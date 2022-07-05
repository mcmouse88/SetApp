package com.mcmouse88.fragmentdialog.level_1

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.mcmouse88.fragmentdialog.R
import com.mcmouse88.fragmentdialog.showToast

/**
 * Если мы хотит использовать диалог внутри фрагмента, то нужно чтобы класс наследовался от
 * класса [DialogFragment], также нужно обязательно переопределить метод [onCreateDialog()], в
 * котором создать сам диалог, и вернуть его. Преимущество использования диалога с фрагментами,
 * это то, что при смене конфигурации диалог не пропадает. Также для [DialogFragment] нужно
 * отдельно создавать и вызывать методы показа диалога и слушателя событий, так как при
 * смене конфигурации слушатель событий не сработает
 */
class SimpleDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DialogInterface.OnClickListener {_, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(KEY_RESPONSE to which)
            )
        }
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(R.string.default_alert_title)
            .setMessage(R.string.default_alert_message)
            .setPositiveButton(R.string.action_yes, listener)
            .setNegativeButton(R.string.action_no, listener)
            .setNeutralButton(R.string.action_ignore, listener)
            .create()
    }

    /**
     * Чтобы слушать события dismiss или cancel, не нужно теперь вызывать методы listener, для
     * этого можно переопределить методы [onDismiss] и [onCancel]
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "Dialog Dismissed")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        showToast(R.string.dialog_cancelled)
    }

    /**
     * Если для обычных фрагментов нужно использование контейнера в активити, и его нужно явно
     * указывать, то для [DialogFragment] используются специальные уникальные тэги. Также чтобы
     * передавать и принимать информацию между диалогом и Активити(фрагментом) используются ключи
     * и объекты [Bundle] (также как и при передаче данных между фрагментами при переходе).
     * Передача информации между [DialogFragment] и Активити(фрагментом) осуществляется через
     * метод [setFragmentResult()], а Активити (фрагмент) принимает через метод
     * [setFragmentResultListener()]. В качестве параметра передается [supportFragmentManager] для
     * Активити, и [parentFragmentManager] для фрагмента
     */
    companion object {
        val TAG = SimpleDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:defaultRequestKey"
        const val KEY_RESPONSE = "key_response"
    }
}