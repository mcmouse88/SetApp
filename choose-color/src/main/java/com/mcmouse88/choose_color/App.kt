package com.mcmouse88.choose_color

import android.app.Application
import com.mcmouse88.choose_color.model.colors.InMemoryColorsRepository
import com.mcmouse88.foundation.BaseApplication
import com.mcmouse88.foundation.model.coroutines.DefaultDispatcher
import com.mcmouse88.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Точка входа в наше приложение (Обязательно прописать его в манифесте). Является singleton
 * scope и содержит модели (А именно пока что только репозиторий, который отвечает за выбор и
 * хранение цвета)
 */
class App : Application(), BaseApplication {

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val defaultDispatcher = DefaultDispatcher(Dispatchers.Default)

    override val singletonScopeDependencies = listOf(
        InMemoryColorsRepository(ioDispatcher)
    )
}