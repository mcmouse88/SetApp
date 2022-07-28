package com.mcmouse88.choose_color

import android.app.Application
import com.mcmouse88.choose_color.model.colors.InMemoryColorsRepository
import com.mcmouse88.foundation.BaseApplication
import com.mcmouse88.foundation.model.Repository
import com.mcmouse88.foundation.model.tasks.TasksFactoryImpl

/**
 * Точка входа в наше приложение (Обязательно прописать его в манифесте). Является singleton
 * scope и содержит модели (А именно пока что только репозиторий, который отвечает за выбор и
 * хранение цвета)
 */
class App : Application(), BaseApplication {

    private val tasksFactory = TasksFactoryImpl()

    override val repositories = listOf(
        tasksFactory,
        InMemoryColorsRepository(tasksFactory)

    )
}