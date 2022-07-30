package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.FinalResult
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import com.mcmouse88.foundation.model.tasks.factories.TasksFactory
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.sideeffect.resourses.Resources
import com.mcmouse88.foundation.sideeffect.toasts.Toasts
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.foundation.views.LiveResult
import com.mcmouse88.foundation.views.MediatorLiveResult
import com.mcmouse88.foundation.views.MutableLiveResult

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    private val tasksFactory: TasksFactory,
    savedStateHandle: SavedStateHandle,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher), ColorsAdapter.Listener {

    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getLiveData("current_color_id", screen.currentColorId)

    private val _saveProgress = MutableLiveData(false)

    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState>
        get() = _viewState

    val screenTitle: LiveData<String> = Transformations.map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            resources.getString(R.string.changed_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.changed_color_screen_title_simple)
        }
    }

    init {
        load()
        _viewState.addSource(_availableColors) { mergeSource() }
        _viewState.addSource(_currentColorId) { mergeSource() }
        _viewState.addSource(_saveProgress) { mergeSource() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        _saveProgress.postValue(true)
        tasksFactory.createTask {
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId).await()
            colorsRepository.setCurrentColor(currentColor).await()
            return@createTask currentColor
        }
            .safeEnqueue(::onSaved) // { onSaved(it) }
    // Так как метод safeEnqueue в лямбду передает result, а метод onSaved в качестве параметра его принимает запись можно сократить
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    private fun mergeSource() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveProgress.value ?: return

        _viewState.value = colors.mapResult { listColors ->
            ViewState(
                colorsList = listColors.map {
                    NamedColorListItem(it, currentColorId == it.id)
                },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showProgressBar = saveInProgress
            )

        }

        /*val currentColor = colors.first { it.id == currentColorId }
        _screenTitle.value =
            uiActions.getString(R.string.changed_color_screen_title, currentColor.name)*/
    }

    private fun load() {
        colorsRepository.getAvailableColors().into(_availableColors)
    }

    private fun onSaved(result: FinalResult<NamedColor>) {
        _saveProgress.value = false
        when(result) {
            is SuccessResult -> navigator.goBack(result.data)
            is ErrorResult -> toasts.showToast(resources.getString(R.string.error_happened))
        }
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