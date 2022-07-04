package com.mcmouse88.anotherkindofapp

import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.anotherkindofapp.databinding.ActivityDialogLevel2Binding
import com.mcmouse88.anotherkindofapp.databinding.PartVolumeBinding
import com.mcmouse88.anotherkindofapp.databinding.PartVolumeInputBinding
import com.mcmouse88.anotherkindofapp.entity.AvailableVolumeValues
import kotlin.properties.Delegates

class DialogLevel2Activity : AppCompatActivity() {

    private var _binding: ActivityDialogLevel2Binding? = null
    private val binding: ActivityDialogLevel2Binding
        get() = _binding ?: throw NullPointerException("ActivityDialogLevel2Binding is null")

    private var volume by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDialogLevel2Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.btShowCustomDialog.setOnClickListener { showCustomAlertDialog() }
        binding.btShowCustomSingleChoiceDialog.setOnClickListener { showCustomDialogSingleChoice() }
        binding.btShowInputDialog.setOnClickListener { showCustomInputDialog() }

        volume = savedInstanceState?.getInt(KEY_VOLUME) ?: 50
        updateUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_VOLUME, volume)
    }

    /**
     * Чтобы передать свою [View] в диалог используется метод [setView()]
     */
    private fun showCustomAlertDialog() {
        val dialogBinding = PartVolumeBinding.inflate(layoutInflater)
        dialogBinding.seekbarVolume.progress = volume
        val dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(R.string.volume_setup)
            .setMessage(R.string.volume_setup_message)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm) {_, _ ->
                volume = dialogBinding.seekbarVolume.progress
                updateUI()
            }
            .create()
        dialog.show()
    }

    /**
     * Сложный диалог с одиночным выбором, также устанавливаем в диалог свою [View], и
     * используем адаптер, для того, чтобы отобразить [View] в виде списка. Также один
     * нюанс, что [View], которая используется в адаптере должна реализовывать интерфейс
     * [Checkable], поэтомы мы ооздали свою собственную [ViewGroup] которая наследуется
     * от [ConstrainLayout], и реализует интерфейс [Checkable]
     *
     */
    private fun showCustomDialogSingleChoice() {
        val volumeItems = AvailableVolumeValues.createVolumeValues(volume)
        val adapter = VolumeAdapter(volumeItems.values)

        var volume = this.volume
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(adapter, volumeItems.currentIndex) { _, which ->
                volume = adapter.getItem(which)
            }
            .setPositiveButton(R.string.action_confirm) {_, _ ->
                this.volume = volume
                updateUI()
            }
            .create()
        dialog.show()
    }

    /**
     * метод [requestFocus()], вызванный у нашего editText отображает выделенным текущее
     * значение переменной volume. Метод [setOnShowListener] слушает события, которые происходят
     * уже при открытом диалоге.
     */
    private fun showCustomInputDialog() {
        val dialogBinding = PartVolumeInputBinding.inflate(layoutInflater)
        dialogBinding.etVolumeInputLevelTwo.setText(volume.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm, null)
            .create()
        dialog.setOnShowListener {
            dialogBinding.etVolumeInputLevelTwo.requestFocus()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredText = dialogBinding.etVolumeInputLevelTwo.text.toString()
                if (enteredText.isBlank()) {
                    dialogBinding.etVolumeInputLevelTwo.error = getString(R.string.enter_volume)
                    return@setOnClickListener
                }
                val volume = enteredText.toIntOrNull()
                if(volume == null || volume > 100) {
                    dialogBinding.etVolumeInputLevelTwo.error = getString(R.string.invalid_value)
                    return@setOnClickListener
                }
                this.volume = volume
                updateUI()
                dialog.dismiss()
            }
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun updateUI() {
        binding.tvCurrentVolumeLevelTwo.text = getString(R.string.current_volume, volume)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val KEY_VOLUME = "key_volume"
    }
}