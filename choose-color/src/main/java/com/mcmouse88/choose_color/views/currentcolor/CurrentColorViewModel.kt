package com.mcmouse88.choose_color.views.currentcolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorListener
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveData<NamedColor>()
    val currentColor: LiveData<NamedColor>
        get() = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(it)
    }

    init {
        colorsRepository.addListener(colorListener)
    }

    override fun onCleared() {
        colorsRepository.removeListener(colorListener)
        super.onCleared()
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.showToast(message)
        }
    }

    fun changeColor() {
        val currentColor = _currentColor.value ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }
}