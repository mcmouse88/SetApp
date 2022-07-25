package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.*
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.ViewState
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.foundation.views.LiveResult
import com.mcmouse88.foundation.views.MediatorLiveResult
import com.mcmouse88.foundation.views.MutableLiveResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
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
            uiActions.getString(R.string.changed_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.changed_color_screen_title_simple)
        }
    }

    private var mockError = false

    init {
        viewModelScope.launch {
            delay(2_000)
            // _availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
            _availableColors.value = ErrorResult(RuntimeException())
        }

        _viewState.addSource(_availableColors) { mergeSource() }
        _viewState.addSource(_currentColorId) { mergeSource() }
        _viewState.addSource(_saveProgress) { mergeSource() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        viewModelScope.launch {
            _saveProgress.postValue(true)
            delay(2_000)
            if (!mockError) {
                _saveProgress.postValue(false)
                uiActions.showToast(uiActions.getString(R.string.error_happened))
                mockError = true
            } else {
                val currentColorId = _currentColorId.value ?: return@launch
                val currentColor = colorsRepository.getById(currentColorId)
                colorsRepository.currentColor = currentColor
                navigator.goBack(result = currentColor)
            }
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(2_000)
            _availableColors.postValue(SuccessResult(colorsRepository.getAvailableColors()))
        }
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
}