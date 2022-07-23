package com.mcmouse88.foundation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.mcmouse88.foundation.navigator.IntermediateNavigator
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.utils.ResourceActions

const val ARG_SCREEN = "arg_screen"


/**
 * Не является обычной [ViewModel], так как она зависит от классов android sdk, и наследуется от
 * [AndroidViewModel], мы ее используем не как обычную [ViewModel], а как место для реализации
 * [Navigator] и [UiActions]. Здесь мы не можем безопасно выполнять навигацию, так как нам
 * нужно быть уверенным, что на момент выполнения этих действий Активити была активна. Поэтому
 * для навигации мы используем вспомогательный класс [ResourceActions] (свойство
 * [whenActivityActive]), который представляет собой очередь действий, которые запускаются только
 * тогда, когда Активити активно, то есть код который будет выполняться в фигурных скобках
 * методов [launch()] и [goBack()] будет выполняться только в том случае, если Активити
 * активно.
 */
class ActivityScopeViewModel(
    val uiActions: UiActions,
    val navigator: IntermediateNavigator
) : ViewModel(), Navigator by navigator, UiActions by uiActions {

    override fun onCleared() {
        navigator.clear()
        super.onCleared()
    }
}