package com.mcmouse88.choose_color.model.colors

import com.mcmouse88.foundation.model.Repository

typealias ColorListener = (NamedColor) -> Unit

/**
 * Определяет список действий и полей, которые доступны для этого репозитория
 */
interface ColorsRepository : Repository {

    var currentColor: NamedColor

    fun getAvailableColors(): List<NamedColor>

    fun getById(id: Long): NamedColor

    fun addListener(listener: ColorListener)

    fun removeListener(listener: ColorListener)
}