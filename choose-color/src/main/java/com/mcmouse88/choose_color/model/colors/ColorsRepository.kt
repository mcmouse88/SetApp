package com.mcmouse88.choose_color.model.colors

import com.mcmouse88.foundation.model.Repository
import kotlinx.coroutines.flow.Flow

typealias ColorListener = (NamedColor) -> Unit

/**
 * Определяет список действий и полей, которые доступны для этого репозитория
 */
interface ColorsRepository : Repository {

    suspend fun getAvailableColors(): List<NamedColor>

    suspend fun getById(id: Long): NamedColor

    suspend fun getCurrentColor(): NamedColor

    fun setCurrentColor(color: NamedColor): Flow<Int>

    fun listenCurrentColor(): Flow<NamedColor>
}