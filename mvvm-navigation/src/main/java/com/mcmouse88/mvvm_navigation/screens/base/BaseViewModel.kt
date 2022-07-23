package com.mcmouse88.mvvm_navigation.screens.base

import androidx.lifecycle.ViewModel

/**
 * Базовый класс для всех [ViewModel], в котором будет один метод [onResult()], в который будет
 * приходить результат с других экранов.
 */
open class BaseViewModel : ViewModel() {

    open fun onResult(result: Any) {

    }
}