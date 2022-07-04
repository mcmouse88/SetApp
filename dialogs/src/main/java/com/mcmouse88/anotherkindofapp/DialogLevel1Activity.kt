package com.mcmouse88.anotherkindofapp

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.anotherkindofapp.databinding.ActivityDialogLevel1Binding
import com.mcmouse88.anotherkindofapp.entity.AvailableVolumeValues
import kotlin.properties.Delegates.notNull

class DialogLevel1Activity : AppCompatActivity() {

    private var _binding: ActivityDialogLevel1Binding? = null
    private val binding: ActivityDialogLevel1Binding
        get() = _binding ?: throw NullPointerException("ActivityDialogLevel1Binding is null")

    private var volume by notNull<Int>()
    private var color by notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDialogLevel1Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.btShowDefaultDialog.setOnClickListener { showAlertDialog() }
        binding.btShowSingleChoiceDialog.setOnClickListener { showSingleChoiceAlertDialog() }
        binding.btShowSingleChoiceDialogWithConfirmation.setOnClickListener {
            showSingleChoiceDialogWithConfirmation()
        }
        binding.btShowMultiplyChoiceDialog.setOnClickListener {
            showMultiplyChoiceAlertDialog()
        }
        binding.btShowMultiplyChoiceDialogWithConfirmation.setOnClickListener {
            showMultipleChoiceWithConfirmationAlertDialog()
        }

        volume = savedInstanceState?.getInt(KEY_VOLUME) ?: 50
        color = savedInstanceState?.getInt(KEY_COLOR) ?: Color.RED
        updateUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_VOLUME, volume)
        outState.putInt(KEY_COLOR, color)
    }

    /**
     * Самый простой диалог, сначала создадим listener, для слушанья нажатий на кнопки диалога,
     * используется интерфейс [OnClickListener], в лямбду которого попадают два параметра, это
     * сам диалог (заменен на подчеркивание, так как нами здесь не используется) и кнопка, которая
     * была нажата (нами названа which), и в зависимости от нажатой кнопки выполняем определенные
     * действия (в данном случае различные toast сообщения). Метод [setCancelable] означает,
     * можно ли закрыть диалог при нажатии на область экрана вне диалога, или кнопку назад,
     * метод [setIcon] устанавливает рядом с заголовком иконку, метод [setTitle] устанавливает
     * сам заголовок, [setMessage] - сообщение, и также опционально доступно три вида кнопок.
     * Метод [setOnCancelListener] слушает события отмены диалога (в случае setCancelable(true))
     * [setOnDismissListener] срабатывает при любых случаях когда диалог был закрыт
     */
    private fun showAlertDialog() {
        val listener = DialogInterface.OnClickListener { _, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> showToast(R.string.uninstall_confirmed)
                DialogInterface.BUTTON_NEGATIVE -> showToast(R.string.uninstall_rejected)
                DialogInterface.BUTTON_NEUTRAL -> showToast(R.string.uninstall_ignored)
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(R.string.default_alert_title)
            .setMessage(R.string.default_alert_message)
            .setPositiveButton(R.string.action_yes, listener)
            .setNegativeButton(R.string.action_no, listener )
            .setNeutralButton(R.string.action_ignore, listener)
            .setOnCancelListener { showToast(R.string.dialog_cancelled) }
            .setOnDismissListener { Log.d(TAG, "Dialog Dismiss") }
            .create()
        dialog.show()
    }

    /**
     * Чтобы задать диалог с одиночным выбором нужно в билдере вызвать метод [setSingleChoiceItems]
     * в который нужно передать массив с вариантами выбора, и текущий индекс выбранного элемента,
     * а также listener, который сработает при выборе элементаю В его лямбду попадает два параметра
     * это сам диалог и индекс массива
     */
    private fun showSingleChoiceAlertDialog() {
        val volumeItems = AvailableVolumeValues.createVolumeValues(volume)
        val volumeTextItem = volumeItems.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(volumeTextItem, volumeItems.currentIndex) { dialog, which ->
                volume = volumeItems.values[which]
                updateUI()
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    /**
     * Этот диалог аналогичен предыдущему, только при выборе айтема необходимо нажать кнопку
     * подтверждения (если отменить диалог даже при выбранном другом айтеме, то останется
     * предыдущее значение). Для этого установим null в параметр listener и добавим кнопку
     * подтверждения выбора через метод [setPositiveButton], в котором и будем обрабатывать
     * ситуацию выбора и закрытия диалога, таким образом приводим параметр диалог лямбды
     * (по умолчанию это [DialogInterface], и у свойства [listView] вызываем метод
     * [checkedItemPosition] для того, чтобы получить позицию выбранного пользователем
     * элемента
     */
    private fun showSingleChoiceDialogWithConfirmation() {
        val volumeItem = AvailableVolumeValues.createVolumeValues(volume)
        val volumeTextItem = volumeItem.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(volumeTextItem, volumeItem.currentIndex, null)
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                val index = (dialog as AlertDialog).listView.checkedItemPosition
                volume = volumeItem.values[index]
                updateUI()
            }
            .create()
        dialog.show()
    }

    /**
     * Для того, чтобы создать список с ножественным выбором нужно использовать функцию
     * [setMultiChoiceItems], в параметры которого приходят массив или курсор, а также
     * массив типа Boolean, в лямбду попадает три параметра, сам диалог, индекс выбранного
     * элемента (which) и true или false в зависимости checked он или unChecked, и в лямбде
     * уже производить какие-то действия в зависимости от действий пользователя
     */
    private fun showMultiplyChoiceAlertDialog() {
        val colorItem = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkBox = colorComponents.map { it > 0 }.toBooleanArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItem, checkBox) {_, which, isChecked ->
                colorComponents[which] = if (isChecked) 255 else 0
                this.color = Color.rgb(
                    colorComponents[0],
                    colorComponents[1],
                    colorComponents[2]
                )
                updateUI()
            }
            .setPositiveButton(R.string.action_close, null)
            .create()
        dialog.show()
    }

    /**
     * Ну и диалог с подтверждением, это значит, что настройки будут применены только после того,
     * как пользователь нажмет на кнопку принять, в отличии от предыдущего, где настройки менялись
     * сразу при выборе айтема, а кнопка работала только для того, чтобы закрыть диалог
     */
    private fun showMultipleChoiceWithConfirmationAlertDialog() {
        val colorItem = resources.getStringArray(R.array.colors)
        val colorComponent = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )
        val checkBox = colorComponent.map { it > 0 }.toBooleanArray()
        var color: Int = this.color

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItem, checkBox) {_, which, isChecked ->
                colorComponent[which] = if (isChecked) 255 else 0
                color = Color.rgb(
                    colorComponent[0],
                    colorComponent[1],
                    colorComponent[2]
                )
            }
            .setPositiveButton(R.string.action_confirm) {_, _ ->
                this.color = color
                updateUI()
            }
            .create()
        dialog.show()
    }

    private fun showToast(@StringRes messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        binding.tvCurrentVolume.text = getString(R.string.current_volume, volume)
        binding.colorView.setBackgroundColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = DialogLevel1Activity::class.java.simpleName
        private const val KEY_COLOR = "key_color"
        private const val KEY_VOLUME = "key_volume"
    }
}