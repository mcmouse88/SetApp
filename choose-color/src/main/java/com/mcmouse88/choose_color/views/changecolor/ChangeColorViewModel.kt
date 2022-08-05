package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.*
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.sideeffect.resourses.Resources
import com.mcmouse88.foundation.sideeffect.toasts.Toasts
import com.mcmouse88.foundation.views.BaseViewModel
import kotlinx.coroutines.CancellationException
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

    private val _saveProgress = MutableStateFlow(false)

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
     *
     */
    val viewState: Flow<Result<ViewState>>
        get() = combine(
            _availableColors,
            _currentColorId,
            _saveProgress,
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
        if (_saveProgress.value) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = myViewModelScope.launch {
        try {
            _saveProgress.value = true
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor).collect()

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.showToast(resources.getString(R.string.error_happened))
        } finally {
            _saveProgress.value = false
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
        saveInProgress: Boolean
    ): Result<ViewState> {
        return colors.mapResult { listColors ->
            ViewState(
                colorsList = listColors.map {
                    NamedColorListItem(it, currentColorId == it.id)
                },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showProgressBar = saveInProgress
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
        val showProgressBar: Boolean
    )
}