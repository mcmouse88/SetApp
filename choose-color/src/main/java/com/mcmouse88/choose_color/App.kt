package com.mcmouse88.choose_color

import android.app.Application
import com.mcmouse88.choose_color.model.colors.InMemoryColorsRepository
import com.mcmouse88.foundation.BaseApplication
import com.mcmouse88.foundation.model.tasks.ThreadUtils
import com.mcmouse88.foundation.model.tasks.dispatcher.MainThreadDispatcher
import com.mcmouse88.foundation.model.tasks.factories.ExecutorServiceTaskFactory
import com.mcmouse88.foundation.model.tasks.factories.HandlerThreadTasksFactory
import java.util.concurrent.Executors

/**
 * Точка входа в наше приложение (Обязательно прописать его в манифесте). Является singleton
 * scope и содержит модели (А именно пока что только репозиторий, который отвечает за выбор и
 * хранение цвета)
 */
class App : Application(), BaseApplication {

    private val singleExecutorFactory = ExecutorServiceTaskFactory(Executors.newSingleThreadExecutor())
    private val cachedThreadExecutor = ExecutorServiceTaskFactory(Executors.newCachedThreadPool())
    private val handlerThreadTasksFactory = HandlerThreadTasksFactory()

    private val thread = ThreadUtils.DefaultThread()
    private val dispatcher = MainThreadDispatcher()

    override val singletonScopeDependencies = listOf(
        cachedThreadExecutor,
        dispatcher,
        InMemoryColorsRepository(handlerThreadTasksFactory, thread)

    )
}