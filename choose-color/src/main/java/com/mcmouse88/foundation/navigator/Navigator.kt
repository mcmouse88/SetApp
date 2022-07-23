package com.mcmouse88.foundation.navigator

import com.mcmouse88.foundation.views.BaseScreen

/**
 * Позволяет запускать экраны, и возвращаться назад, передавая результат
 */
interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)
}