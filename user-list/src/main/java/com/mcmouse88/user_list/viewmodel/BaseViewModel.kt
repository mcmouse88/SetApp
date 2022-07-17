package com.mcmouse88.user_list.viewmodel

import androidx.lifecycle.ViewModel
import com.mcmouse88.user_list.screens.tasks.Task

/**
 * Для того, чтобы автоматически удалять незавершившиеся задачи, создадим базовый класс
 * [BaseViewModel], от которого будут наследоваться созданные нами ViewModel, и в этом классе
 * в методе [onCleared()] будем отменять задачи
 */
open class BaseViewModel : ViewModel() {

    private val task = mutableListOf<Task<*>>()

    override fun onCleared() {
        task.forEach { it.cancel() }
        super.onCleared()
    }

    /**
     * Создадим extension метод, который будет добавлять задачи, и при уничтожении [ViewModel]
     * эти задачи будут автоматически отменяться
     */
    fun<T> Task<T>.autoCancel() {
        task.add(this)
    }
}