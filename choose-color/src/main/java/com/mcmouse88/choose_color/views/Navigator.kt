package com.mcmouse88.choose_color.views

import com.mcmouse88.choose_color.views.base.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)
}