package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.*
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen
import com.mcmouse88.foundation.model.*
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.sideeffect.resourses.Resources
import com.mcmouse88.foundation.sideeffect.toasts.Toasts
import com.mcmouse88.foundation.utils.finiteShareIn
import com.mcmouse88.foundation.views.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getStateFlowMyExtension("current_color_id", screen.currentColorId)

    private val _instantSaveProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveProgress = MutableStateFlow<Progress>(EmptyProgress)

    /**
     * Для того, чтобы объеденить несколько Flow в один (по примеру как было раньше, LiveDate
     * объединялись через MediatorLiveData)используем функцию [combine()] (есть еще несколько
     * способов). Данная функция принимает на вход несколько Flow(максимально 5), и функцию
     * преобразования. В функцию преобразования попадает набор из последних самых актуальных
     * элементов из всех входящих Flow, а вернуть должна объединенный результат (в нашем случае
     * класс [ViewState]). Таким образом как только в каком-гибудь из входящиъх Flow появится
     * новый элемент, вызовется функция преобразования, и сформируется актуальный результат.
     * Функция преобразования должна принимать в качестве параметров типы, передаваемые во Flow
     * Пример:
     * ```kotlin
     * val a = MutableStateFlow<Result<Boolean>>(false)
     * fun merge(a: Boolean): Result<ViewState> {
     *
     * }
     * ```
     * [SharedFlow] как и [StateFlow] является горячим и бесконечным flow, и работает независимо
     * от того подписан на него кто-либо или нет.
     * Создать [SharedFlow] можно двумя способами. Создать напрямую через класс с конструктором:
     * ```kotlin
     * private val sharedFlow = MutableSharedFlow<String>()
     * ```
     * Конструктор [SharedFlow] принимает три параметра
     * 1. replay: Int
     * 2. extraBufferCapacity: Int
     * 3. onBufferOverflow: SUSPEND | DROP_LATEST | DROP_OLDEST
     *
     * Параметр [replay] определяет какое количество элементов будет хранится в буфере для
     * будущих подписчиков (по умолчанию равен 0). Параметр [extraBufferCapacity] это
     * дополнительное место в буфере поверх того, что указано в replay, который будет хранить
     * определенное в нем количество элементов (replay + extraBufferCapacity), до тех пор пока
     * не начнется политика параметра onBufferOverflow (переполнение буфера). Параметр
     * [onBufferOverflow] определяет что будет происходить с элементами, когда буфер уже полный.
     * значение [SUSPEND](значение по умолчанию) определяет, что если кто-то отправляет элемент
     * через метод emit в тот момент когда буфер переполнен то он заснет либо до тех пор пока не
     * появится место, либо получит уведомление, что буфер переполнен в случает отправки через
     * метод tryEmit. В случае значения DROP_LATEST | DROP_OLDEST отправка будет всегда успешно,
     * но в таком случае элементы будут удаляться из буфера, либо самые старые, либо новые
     * приходящие просто не будут добавляться(в зависимости от выбранного параметра).
     * Второй способ создания [SharedFlow] путом комбинации операторов [buffer](опциональный) и
     * [shareIn] на каком-нибудь уже существующем Flow. В метод [buffer] передаются два
     * параметра bufferCapacity и bufferOverflow, принцип их работы описан выше. Метод [shareIn]
     * принимает три параметра: scope: CoroutineScope, started и replay: Int. Параметр started
     * принимает три значения:
     * 1. Eagerly
     * 2. Lazily
     * 3. WhileSubscribed(stopTimeOutMillis, replayExpirationMillis)
     *
     * [Eagerly] означает ,что flow немедленно стартует выполнение flow на котором был вызван
     * оператор shareIn и начинает сразу испускать элементы. [Lazily] означает, что flow начнет
     * работать тогда, когда появится первый подписчик. [WhileSubscribed] также начнет
     * работу flow при появлении первого подписчика, но плюс к этому будет отменять flow, при
     * отписке всех подписчиков, и запускать flow при появлении подписчиков (в случае если flow
     * было отменено при отсутствии подписчиков, а не завершен). Параметры stopTimeOutMillis
     * -> спустя какое время после отписки отменять flow, replayExpirationMillis -> спустя какое
     * время будет очищен буфер элементов при отписке.
     * Пример передачи елементов в [SharedFlow]:
     * ```kotlin
     * sharedFlow.tryEmit("Some content")
     *
     * viewModelScope.launch {
     *     sharedFlow.emit("Some content")
     * }
     * ```
     * Так как [emit()] является suspend функцией, то ее можно вызвать только внутри корутины
     * (другой suspend функции или suspend лямбды)
     */
    val viewState: Flow<Result<ViewState>>
        get() = combine(
            _availableColors,
            _currentColorId,
            _instantSaveProgress,
            _sampledSaveProgress,
            ::mergeSource
        )

    val screenTitle: LiveData<String> = viewState.map { result ->
        return@map if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            resources.getString(R.string.changed_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.changed_color_screen_title_simple)
        }
    }.asLiveData()

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    /**
     * Так как функция [setCurrentColor()] возвращает нам холодное flow то получение данных будет
     * происходить дважды (вернее столько раз, сколько будет вызыван на нем метод collect),
     * например если бы мы получали данные по сети или из БД, то мы бы делали два запроса в БД или
     * сеть, что не очень оптимизировано и будет тратить много ресурсов памяти, плюс к этому
     * время на получение данных, так как эти операции не мгновенные. Чтобы этого избежать,
     * превратим его в [SharedFlow] вызвав на нем метод [shareIn].
     */
    fun onSavePressed() = myViewModelScope.launch {
        try {
            _instantSaveProgress.value = PercentageProgress.START
            _sampledSaveProgress.value = PercentageProgress.START
            val currentColorId =
                _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            val flow = colorsRepository.setCurrentColor(currentColor)
                .finiteShareIn(this)

            val instantJob = async {
                flow.collect { percentage ->
                    _instantSaveProgress.value = PercentageProgress(percentage)
                }
            }

            val sampleJob = async {
                flow.sample(200).collect {percentage ->
                    _sampledSaveProgress.value = PercentageProgress(percentage)
                }
            }

            instantJob.await()
            sampleJob.await()

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.showToast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveProgress.value = EmptyProgress
            _sampledSaveProgress.value = EmptyProgress
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    private fun mergeSource(
        colors: Result<List<NamedColor>>,
        currentColorId: Long,
        instantSaveInProgress: Progress,
        sampleSavedProgress: Progress
    ): Result<ViewState> {
        return colors.mapResult { listColors ->
            ViewState(
                colorsList = listColors.map {
                    NamedColorListItem(it, currentColorId == it.id)
                },
                showSaveButton = !instantSaveInProgress.isInProgress(),
                showCancelButton = !instantSaveInProgress.isInProgress(),
                showProgressBar = instantSaveInProgress.isInProgress(),

                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(
                    R.string.percentage_value,
                    sampleSavedProgress.getPercentage()
                )
            )

        }
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    /**
     * data class для отображения интерфейса в зависимости от состояния результатов
     */
    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String
    )
}