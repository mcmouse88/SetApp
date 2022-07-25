package com.mcmouse88.choose_color.views.currentcolor

import androidx.lifecycle.viewModelScope
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.model.colors.ColorListener
import com.mcmouse88.choose_color.model.colors.ColorsRepository
import com.mcmouse88.choose_color.model.colors.NamedColor
import com.mcmouse88.choose_color.views.changecolor.ChangeColorFragment
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.model.takeSuccess
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.views.BaseViewModel
import com.mcmouse88.foundation.views.LiveResult
import com.mcmouse88.foundation.views.MutableLiveResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

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

    /**
     * Сымитируем при первом вызове ошибку при получении результата, чтобы отобразить возможность
     * повторить запрос, при повторном запросе в методе [tryAgain()] будет возвращен
     * успешный результат
     */
    init {
        viewModelScope.launch {
            delay(2_000)
            // colorsRepository.addListener(colorListener)
            _currentColor.postValue(ErrorResult(RuntimeException()))
        }
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
        val currentColor = _currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        viewModelScope.launch {
            _currentColor.postValue(PendingResult())
            delay(2_000)
            colorsRepository.addListener(colorListener)
        }
    }
}