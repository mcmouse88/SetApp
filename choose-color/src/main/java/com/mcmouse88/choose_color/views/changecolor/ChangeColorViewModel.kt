package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.*
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.sideeffect.resourses.Resources
import com.mcmouse88.foundation.sideeffect.toasts.Toasts
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.foundation.views.LiveResult
import com.mcmouse88.foundation.views.MediatorLiveResult
import com.mcmouse88.foundation.views.MutableLiveResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

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

    fun onSavePressed() = myViewModelScope.launch {
        try {
            _saveProgress.postValue(true)
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor)

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