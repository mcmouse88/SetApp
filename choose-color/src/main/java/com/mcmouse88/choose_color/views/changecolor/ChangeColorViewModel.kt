package com.mcmouse88.choose_color.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    private val _availableColors = MutableLiveData<List<NamedColor>>()
    private val _currentColorId =
        savedStateHandle.getLiveData("current_color_id", screen.currentColorId)

    private val _colorList = MediatorLiveData<List<NamedColorListItem>>()
    val colorList: LiveData<List<NamedColorListItem>>
        get() = _colorList

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String>
        get() = _screenTitle

    init {
        _availableColors.value = colorsRepository.getAvailableColors()
        _colorList.addSource(_availableColors) { mergeSource() }
        _colorList.addSource(_currentColorId) { mergeSource() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        val currentColorId = _currentColorId.value ?: return
        val currentColor = colorsRepository.getById(currentColorId)
        colorsRepository.currentColor = currentColor
        navigator.goBack(result = currentColor)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    private fun mergeSource() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val currentColor = colors.first { it.id == currentColorId }
        _colorList.value = colors.map { NamedColorListItem(it, currentColorId == it.id) }
        _screenTitle.value =
            uiActions.getString(R.string.changed_color_screen_title, currentColor.name)
    }
}