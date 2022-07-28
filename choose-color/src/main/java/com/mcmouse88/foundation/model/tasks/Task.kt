package com.mcmouse88.foundation.model.tasks

import com.mcmouse88.foundation.model.FinalResult
import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher

typealias TaskListener<T> = (FinalResult<T>) -> Unit

interface Task<T> {

    /**
     * Метод который будет возвращать результат синхронно
     */
    fun await(): T

    /**
     * Метод для отмены задачи(например если пользователь закрыл приложение или вышел с экрана, и
     * нет необходимости выполнять задачу
     */
    fun cancel()

    /**
     * Метод, который будет выполнять задачу асинхронно. Listeners будут запускаться только в
     * главном потоке
     */
    fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>)
}