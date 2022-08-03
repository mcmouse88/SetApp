package com.mcmouse88.choose_color.views.currentcolor

import android.Manifest
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorListener
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.model.takeSuccess
import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import com.mcmouse88.foundation.model.tasks.factories.TasksFactory
import com.mcmouse88.foundation.sideeffect.dialogs.Dialogs
import com.mcmouse88.foundation.sideeffect.dialogs.plugin.DialogConfig
import com.mcmouse88.foundation.sideeffect.intents.Intents
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.sideeffect.permissions.Permissions
import com.mcmouse88.foundation.sideeffect.permissions.plugin.PermissionStatus
import com.mcmouse88.foundation.sideeffect.resourses.Resources
import com.mcmouse88.foundation.sideeffect.toasts.Toasts
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.foundation.views.LiveResult
import com.mcmouse88.foundation.views.MutableLiveResult

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val taskFactory: TasksFactory,
    private val colorsRepository: ColorsRepository,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher) {

    /**
     * Так как изначально у нас еще данных нет, мы можем сразу передать в LiveData
     * [PendingResult]
     */
    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor>
        get() = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }


    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onCleared() {
        colorsRepository.removeListener(colorListener)
        super.onCleared()
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = resources.getString(R.string.changed_color, result.name)
            toasts.showToast(message)
        }
    }

    fun changeColor() {
        val currentColor = _currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        load()
    }

    /**
     * При запуске side effect на запрос пермишена, который запускается по нажатию на кнопку,
     * мы сначала проверяем есть ли у нас уже пермишен (для примера запрашиваем разрешение на
     * ACCESS_FINE_LOCATION). Если пермишен уже есть, то мы просто показываем диалог, что данный
     * пермишен уже есть. Иначе мы делаем запрос пермишена, и ждем результата (действия
     * пользователя по добавлению или отклонению разрешения). В зависимости от выбора пользователя
     * выполняем определенные действия. Если пользователь дал разрешение или единократно отклонил,
     * то показываем тост сообщение, если же отклонил навсегда, то показываем диалоговое окно, в
     * котором сообщаем, что разшение заблокировано, и его можно разблокировать в настройках, и в
     * диалоговом окне имеется две кнопки, открыть настройки и отмена. Так как мы ожидаем ответа
     * от пользователя используя task, то до ответа пользоваетеля поток будет заблокирован,
     * поэтому данное действие нельзя выполнять на главном потоке, и поэтому данный код вызывается
     * внутри метода [createTask()]
     */
    fun requestPermissions() = taskFactory.createTask<Unit> {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog()).await()
        } else {
            when(permissions.requestPermission(permission).await()) {
                PermissionStatus.GRANTED -> {
                    toasts.showToast(resources.getString(R.string.permissions_granted))
                }
                PermissionStatus.DENIED -> {
                    toasts.showToast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog()).await()) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }.safeEnqueue()

    private fun load() {
        colorsRepository.getCurrentColor().into(_currentColor)
    }

    private fun createPermissionAlreadyGrantedDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.permissions_already_ganted),
        positiveButton = resources.getString(R.string.action_ok)
    )

    private fun createAskForLaunchingAppSettingsDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.open_app_settings_message),
        positiveButton = resources.getString(R.string.action_open),
        negativeButton = resources.getString(R.string.action_cancel)
    )
}