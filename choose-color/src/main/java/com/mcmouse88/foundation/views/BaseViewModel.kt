package com.mcmouse88.foundation.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.TaskListener
import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import com.mcmouse88.foundation.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Для удобства работы с результатом создадим три typealias
 */
typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Базовый класс для всех ViewModel (кроме [MainViewModel], который содержив себе опциональный
 * метод [onResult()], на случай если фрагменту нужно получить результат.
 */
open class BaseViewModel(
    private val dispatcher: Dispatcher
) : ViewModel() {

    /**
     * Приватное поле, которое будет в себе содержать список задач
     */
    private val tasks = mutableSetOf<Task<*>>()

    open fun onResult(result: Any) {

    }

    fun onBackPressed() {
        clearTasks()
    }

    fun<T> Task<T>.safeEnqueue(listener: TaskListener<T>? = null) {
        tasks.add(this)
        this.enqueue(dispatcher) {
            tasks.remove(this)
            listener?.invoke(it)
        }
    }

    fun<T> Task<T>.into(liveResult: MutableLiveResult<T>) {
        liveResult.value = PendingResult()
        this.safeEnqueue {
            liveResult.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearTasks()
    }

    private fun clearTasks() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }
}