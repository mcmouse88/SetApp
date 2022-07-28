package com.mcmouse88.choose_color.model.colors

import com.mcmouse88.foundation.model.Repository
import com.mcmouse88.foundation.model.tasks.Task

typealias ColorListener = (NamedColor) -> Unit

/**
 * Определяет список действий и полей, которые доступны для этого репозитория
 */
interface ColorsRepository : Repository {

    fun getAvailableColors(): Task<List<NamedColor>>

    fun getById(id: Long): Task<NamedColor>

    fun getCurrentColor(): Task<NamedColor>

    fun setCurrentColor(color: NamedColor): Task<Unit>

    fun addListener(listener: ColorListener)

    fun removeListener(listener: ColorListener)
}