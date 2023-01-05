package com.mcmouse88.mvvm_navigation.navigator

import androidx.annotation.StringRes
import com.mcmouse88.mvvm_navigation.screens.base.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)

    fun showToast(@StringRes messageRes: Int)

    /**
     * Метод предназначен для того, чтобы во [ViewModel] можно было получить строку по
     * идентификатору
     */
    fun getString(@StringRes messageRes: Int): String
}