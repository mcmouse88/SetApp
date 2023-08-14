package com.mcmouse88.choose_color.views.currentcolor

import android.Manifest
import androidx.lifecycle.viewModelScope
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.model.takeSuccess
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
import kotlinx.coroutines.*

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val colorsRepository: ColorsRepository,
) : BaseViewModel() {

    /**
     * Так как изначально у нас еще данных нет, мы можем сразу передать в LiveData
     * [PendingResult]
     */
    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor>
        get() = _currentColor


    /**
     * Чтобы запустить корутину, достаточно у объекта реализующего ирнтерфейс [CoroutineScope]
     * вызвать extension метод [launch()] (метод ничего не возвращает) либо extension метод
     * [async()] (этот метод может вернуть какое-либо значение). Внутри этих методов можно писать
     * любой асинхронный код. В данном блоке обычно вызываются suspend функции, это функция,
     * которая используется только внутри suspend лямбды (метод launch и async, в качестве
     * параметра принимают suspend лямбду), либо внутри других suspend функций. Пример реализации
     * метода launch:
     * ```kotlin
     * public fun CoroutineScope.launch(
     *     context: CoroutineContext = EmptyCoroutineContext,
     *     start: CoroutineStart = CoroutineStart.DEFAULT,
     *     block: suspend CoroutineScope.() -> Unit
     * ): Job {
     *     val newContext = newCoroutineContext(context)
     *     val coroutine = if (start.isLazy)
     *        LazyStandaloneCoroutine(newContext, block) else
     *        StandaloneCoroutine(newContext, active = true)
     *     coroutine.start(start, coroutine, block)
     *     return coroutine
     * }
     * ```
     * Данная
     * функция может быть приостановлена, а затем возобновлена. Scope это такой объект, который
     * во первых задает время жизни корутин, во вторых содержит ссылку на [CoroutineContext], и в
     * третьих определяет методы для запуска корутин launch и async. Scope определяет время жизни
     * корутины, в данном случае [viewModelScope] будте жить пока жива ViewModel внутри которой он
     * вызван, а [CoroutineContext] определяет где и как корутина будет работать. Контекст состоит
     * из следующих элементов:
     * 1. Job - основной и обязательный, представляет собой объект,
     * который контролирует и отображает выполнение асинхронного кода.
     * 2. CoroutineName - опциональный, название корутины
     * 3. CoroutineDispatcher - опциональный, определяет где и как будет выполняться корутина
     * 4. CoroutineExceptionHandler - опциональный, определяет логику обработки ошибок.
     *
     * Доступ к контексту можно получить просто вызвав свойство [coroutineContext]. Конкретную
     * работу которую выполняет корутина представляет объект типа [Job], находящийся внутри контекста.
     * Метод [async()] возвращает объект типа [Deferred<T>], который наследуется от класса Job.
     * Когда мы вызываем метод launch или async создается новый [СoroutineContext], в котором
     * также создается новый объект типа [Job]. Если вызывать внутри методов launch или async эти
     * методы, то также будет создаваться новый контекст и новый job, которые будут наследоваться
     * от контекста первого метода, внутри которого была вызвана функция. Новые контексты будут
     * иметь те же параметры что и их родитель, за исключением параметров, которые были
     * переопределены при запуске метода. Пример:
     * ```kotlin
     * viewModelScope.launch {
     *     delay(1_000)
     *     launch(Dispatchers.IO) {
     *         delay(1_000)
     *         withContext(Dispatchers.Main) {
     *             delay(1_000)
     *         }
     *     }
     * }
     * ```
     * В данном случае второй контекст, созданный при вызове метода launch унаследует все параметры
     * родителя, кроме параметра Dispatcher, потому что он был явно указан при вызове этой функции.
     * Также мы еще можем внутри вызвать метод [withContext()]. Также как и с методами launch и
     * async будет создан новый контекст и новый job, который унаследуется от ближайшего родителя,
     * но его параметры также могут быть переопределены.
     * <br>Отличия методов корутины:</br>
     * 1. launch(): Job - просто запускает блок кода асинхронно
     * - Можно подождать завершения с помощью метода Job.join()
     * - Метод join() - это suspend fun
     * 2. async(): Deferred<T> - запускает блок кода асинхронно и позволяет вернуть результат
     * - Можно подождать результата с помощью метода Deferred.await()
     * - Метод await() - это тоже suspend fun
     * 3. withContext(): T - запучкает блок кода и ожидает результат этого блока,
     * может быть вызван только внутри метода launch или async
     * Если выполнение job родителя будет отменено, то job наследников также будет отменен.
     * Пример использования методов launch, async и withContext:
     * ```kotlin
     * viewModelScope.launch {
     *     delay(1_000)
     *
     *     val result = withContext(Dispatchers.Default) {
     *         val part1 = async {
     *             delay(1_000)
     *             return@async "Part1 done"
     *         }
     *         val part2 = async {
     *             delay(2_000)
     *             return@async "Part2 done"
     *         }
     *         val part3 = async {
     *             delay(3_000)
     *             return@async "Part3 done"
     *         }
     *         val result1 = part1.await()
     *         val result2 = part2.await()
     *         val result3 = part3.await()
     *
     *         return@withContext "$result1\n$result2\n$result3"
     *     }
     *     Log.e("TAG_RESULT", "Result: $result")
     *}
     */
    init {

        viewModelScope.launch {
            colorsRepository.listenCurrentColor().collect {
                _currentColor.postValue(SuccessResult(it))
            }
        }
        load()
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
    fun requestPermissions() = myViewModelScope.launch {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog())
        } else {
            when(permissions.requestPermission(permission)) {
                PermissionStatus.GRANTED -> {
                    toasts.showToast(resources.getString(R.string.permissions_granted))
                }
                PermissionStatus.DENIED -> {
                    toasts.showToast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog())) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }

    private fun load() = into(_currentColor) { colorsRepository.getCurrentColor() }

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